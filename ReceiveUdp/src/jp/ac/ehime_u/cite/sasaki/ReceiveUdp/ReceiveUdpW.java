package jp.ac.ehime_u.cite.sasaki.ReceiveUdp;

import java.awt.FlowLayout;
import java.net.*;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class ReceiveUdpW extends JFrame {
	private static final int PORT = 12345;

	private final static String TITLE = "ReceiveUdpW by Takashi SASAKI";
	private final static String OPENING = "ReceiveUdpW v1.2 by Takashi SASAKI, 2011\n"
			+ "Listening all interfaces for "
			+ PORT
			+ "/udp.\n"
			+ "-------------------------------------------\n";
	private JTextArea jTextArea;
	private JLabel jLabel;

	public ReceiveUdpW() {
		// フレームにレイアウトを設定
		getContentPane().setLayout(new FlowLayout());

		// テキストエリアを作成
		jTextArea = new JTextArea(OPENING, 30, 60) {
			// appendメソッドをオーバーライドして常に最終行が表示されるようにする
			@Override
			public void append(String str) {
				super.append(str);
				jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
			}
		};
		jTextArea.setLineWrap(true);

		// フレームにスクロールペインを設定
		JScrollPane j_scroll_pane = new JScrollPane(jTextArea);

		getContentPane().add(j_scroll_pane);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(TITLE);
		// setSize(300, 400);
		setVisible(true);
		jLabel = new JLabel();
		getContentPane().add(jLabel);
		jLabel.setText("" + PORT);
		pack();
	}

	public static void main(String[] argv) throws Exception {
		// Swingのフレームを表示
		ReceiveUdpW receive_udp = new ReceiveUdpW();

		// ワイルドカードアドレスで待ち受け
		DatagramSocket datagram_socket = new DatagramSocket(PORT);

		// 受け付けるデータバッファとUDPパケットを作成
		byte buffer[] = new byte[1024];
		DatagramPacket datagram_packet = new DatagramPacket(buffer,
				buffer.length);

		while (true) {
			// UDPパケットを受信
			datagram_socket.receive(datagram_packet);
			Date received_date = new Date();
			InetAddress inet_address = datagram_packet.getAddress();
			String sender_address = inet_address.getHostAddress();
			int sender_port = datagram_packet.getPort();
			String received_data = new String(datagram_packet.getData(), 0,
					datagram_packet.getLength());
			// 表示する文字列を作成
			String s = "[" + sender_address + ":" + sender_port + ":"
					+ received_date.getTime() + "] " + received_data;
			// 受信したデータを標準出力へ出力
			System.out.println(s);
			// JTextAreaにも表示
			receive_udp.jTextArea.append(s);
		}
	}
}
