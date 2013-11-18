package jp.ac.ehime_u.cite.sasaki.SendUdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SendUdp extends Activity implements OnClickListener{
	private String destination_host;
	private int destination_port;
	private DatagramSocket datagramSocket;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button button_send_udp = (Button) this
				.findViewById(R.id.ButtonSendToggle);
		button_send_udp.setOnClickListener(this);

		// ソケットを用意
		try {
			datagramSocket = new DatagramSocket();
		} catch (SocketException e) {
			//e.printStackTrace();
			Log.v("SendUdp#onCreate", e.toString());
		}
	}

	//送信ボタンが押されたときに呼び出されるメソッド
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ButtonSendToggle:
			SendDebugMessageByUdp();
			Log.v("SensorUdp#onClick", "ButtonSendDebugMessage");
			break;
		}
	}
	
	// 実際にUDPデータグラムを送信するメソッド
	// onClick から呼び出されるだけで、クラスの外から呼び出されることは無いため、
	// private アクセス指定子でアクセスを制限している。
	private void SendDebugMessageByUdp() {		
		//EditTextビューからの宛先ホストアドレスの取得
		EditText edit_text_host = (EditText) this
				.findViewById(R.id.EditTextHost);
				Editable editable_host = edit_text_host.getEditableText();		
		this.destination_host = editable_host.toString();
		
		//EditTextビューからの宛先UDPポート番号の取得
		EditText edit_text_port = (EditText) this
				.findViewById(R.id.EditTextPort);
		Editable editable_port = edit_text_port.getEditableText();
		String string_port = editable_port.toString();
		this.destination_port = Integer.parseInt(string_port);
		
		//EditTextビューからのメッセージの取得
		EditText edit_text_debug_message = (EditText) this
				.findViewById(R.id.EditTextDebugMessage);
		Editable editable = edit_text_debug_message.getText();
		String string = editable.toString();
		byte[] byte_array = string.getBytes();

		try {
			//IPアドレスは InetAddress クラスで表現する
			InetAddress inet_address = InetAddress.getByName(destination_host);
			
			//UDPデータグラムは DatagramPacket クラスで表現する
			DatagramPacket datagram_packet = new DatagramPacket(byte_array,
					byte_array.length, inet_address, destination_port);
			
			//DatagramSocket datagram_socket = new DatagramSocket();
			//ネットワーク入出力口はソケットとして抽象化される
			if(null != datagramSocket){
				//datagramSocket.close();
				datagramSocket = null;
				datagramSocket = new DatagramSocket();
			}
			//DatagramSocket は DatagramPacket を渡されると
			//指定された宛先アドレスに UDP データグラムとして送出する。
			datagramSocket.send(datagram_packet);
		} catch (IOException io_exception) {
			//問題が起きたら例外を捉えてログに出力
			Log.v("SensorUdp#SendDebugMessageByUdp", io_exception.toString());
		}
	}
}
