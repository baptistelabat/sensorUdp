package jp.ac.ehime_u.cite.sasaki.SensorUdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

// クラス内クラスでスレッドオブジェクトを実装
// Thread オブジェクトを継承するか Runnable インターフェイスを実装する
class ReceiverThread extends Thread {

    static final int ERROR_WAIT = 5000; // wait when failed to receive
    static final int MAX_LINES = 100;
    static final int BUFFER_SIZE = 2000;
    static final String TAG = "SensorUdp";

    volatile Handler handler;
    volatile boolean toBeContinued = true;
    DatagramSocket datagramSocket;
    DatagramPacket datagramPacket;
    ArrayList<String> receivedLines;
    TextView textViewReceivedLines;
    int incomingPort;
    EditText editTextIncomingPort;

    static ReceiverThread receiverThread;
    static boolean inGetSingleton;

    // シングルトンインスタンスを生成したいのでコンストラクタはプライベート
    ReceiverThread() {
        super();
        toBeContinued = true;
        receivedLines = new ArrayList<String>();
        // 受け付けるデータバッファとUDPパケットを作成
        byte buffer[] = new byte[BUFFER_SIZE];
        datagramPacket = new DatagramPacket(buffer, buffer.length);
    }

    // シングルトンインスタンスを返すスタティックメソッド
    synchronized static public ReceiverThread GetSingleton(Activity activity_) {
        inGetSingleton = true;
        if (receiverThread == null) {
            receiverThread = new ReceiverThread();
        }
        receiverThread.NotifyActivity(activity_);
        inGetSingleton = false;
        return receiverThread;
    }

    synchronized public ReceiverThread GetSingleton() {
        if (inGetSingleton)
            throw new ExceptionInInitializerError();
        return receiverThread;
    }

    void NotifyActivity(Activity activity_) {
        textViewReceivedLines = (TextView) activity_
                .findViewById(R.id.textViewReceivedLines);
        editTextIncomingPort = (EditText) activity_
                .findViewById(R.id.editTextIncomingPort);
        editTextIncomingPort
                .setOnEditorActionListener(new OnEditorActionListener() {
                    public boolean onEditorAction(TextView arg0, int arg1,
                            KeyEvent arg2) {
                        ChangeIncomingPort(Integer.parseInt(arg0.getText()
                                .toString()));
                        return true;
                    }
                });
        editTextIncomingPort
                .setOnFocusChangeListener(new OnFocusChangeListener() {
                    public void onFocusChange(View v, boolean hasFocus) {
                        ChangeIncomingPort(Integer
                                .parseInt(editTextIncomingPort.getText()
                                        .toString()));
                    }
                });
        editTextIncomingPort.onEditorAction(EditorInfo.IME_NULL);
    }

    @Override
    public void interrupt() {
        toBeContinued = false;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        if (datagramSocket != null) {
            datagramSocket.close();
            datagramSocket = null;
        }
        super.interrupt();
    }

    @Override
    public void start() {
        Log.e("ReceiverThread#start", "unexpected arguments");
    }

    // コンストラクタを隠蔽しているので初期化はこのメソッドで行う
    public void start(Handler handler_) {
        this.toBeContinued = true;
        if (!this.isAlive()) {
            this.handler = handler_;
            super.start();
        }
    }

    public void ChangeIncomingPort(int new_incoming_port) {
        incomingPort = new_incoming_port;
        if (datagramSocket != null) {
            datagramSocket.close();
            datagramSocket = null;
        }
        if (!isAlive()) {
            Log.e(TAG,
                    "ReceiverThread#ChangeIncomingPort detected the thread is already dead.");
        }
    }

    public void run() {
        while (toBeContinued == true) {

            if (datagramSocket == null) {
                try {
                    datagramSocket = new DatagramSocket(incomingPort);
                } catch (SocketException e) {
                    Log.w(TAG,
                            "ReceiverThread#run failed to open datagram socket.");
                    try {
                        Thread.sleep(ERROR_WAIT);
                    } catch (InterruptedException e1) {
                        Log.v(TAG,
                                "ReceiverThread#run is recovering from error after timeout.");
                    }
                    continue;
                }
            }

            try {
                datagramSocket.receive(datagramPacket);
            } catch (IOException e) {
                Log.v(TAG,
                        "ReceiverThread#run catched IOException from DatagramSocket#receive.");
                try {
                    Thread.sleep(ERROR_WAIT);
                } catch (InterruptedException e1) {
                    Log.v(TAG,
                            "ReceiverThread#run is recovering from error after timeout.");
                }
                continue;
            }

            InetAddress inet_address = datagramPacket.getAddress();
            String sender_address = inet_address.getHostAddress();
            int sender_port = datagramPacket.getPort();
            String received_data = new String(datagramPacket.getData(), 0,
                    datagramPacket.getLength());

            // 受信したデータをアレイリストに追加
            if (receivedLines.size() >= MAX_LINES) {
                receivedLines.remove(0);
            }
            receivedLines.add("[" + sender_address + ":" + sender_port + "]"
                    + received_data);

            // 匿名オブジェクトを使って擬似的なクロージャを実現するテクニック。
            handler.post(new Runnable() {
                public void run() {
                    // テキストビューを更新
                    String s = new String(); // ストリームを使うほうがスマート
                    for (int i = receivedLines.size() - 1; i >= 0; --i) {
                        s = s + receivedLines.get(i) + "\n";
                    }
                    textViewReceivedLines.setText(s);
                }
            });
        }
    }
}