package com.longjiabo.fund.util;
@Deprecated
public class MailUtil {
//	private static final Logger log = LoggerFactory.getLogger(MailUtil.class);
//	final static String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
//
//	public static void sendMail(String content) {
//		log.info("send email");
//		boolean isDev = LettyUtils.isdev();
//		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
//		String subject = sdf.format(new Date()) + " Report";
//		String from = isDev ? PropertiesUtil.getProp("test_from") : PropertiesUtil.getProp("from");
//		String to = isDev ? PropertiesUtil.getProp("test_to") : PropertiesUtil.getProp("to");
//		List<String> toList = Arrays.asList(to.split(","));
//		String cc = isDev ? PropertiesUtil.getProp("test_cc") : PropertiesUtil.getProp("cc");
//		List<String> ccList = null;
//		if (cc != null) {
//			ccList = Arrays.asList(cc.split(","));
//		}
//		try {
//			sendMail(subject, from, toList, ccList, null, content);
//		} catch (MessagingException e) {
//			log.error("", e);
//		}
//	}
//
//	private static void sendMail(String subject, String from, List<String> toList, List<String> ccList,
//			List<String> bccList, String content) throws MessagingException {
//		if (from != null && toList != null && toList.size() > 0) {
//			Properties prop = new Properties();
//			prop.setProperty("mail.smtp.host", PropertiesUtil.getProp("mail.smtp.host"));
//			boolean auth = "true".equals(PropertiesUtil.getProp("SMTPAuth"));
//			Session session = null;
//			if (auth) {
//				Authenticator au = new Authenticator() {
//					protected PasswordAuthentication getPasswordAuthentication() {
//						return new PasswordAuthentication("haonotifier", "ocdNew2");
//					}
//				};
//				prop.put("mail.smtp.auth", "true");
//				// prop.setProperty("mail.smtp.socketFactory.class",
//				// SSL_FACTORY);
//				// prop.setProperty("mail.smtp.socketFactory.fallback",
//				// "false");
//				// prop.setProperty("mail.smtp.port", "465");
//				// prop.setProperty("mail.smtp.socketFactory.port", "465");
//				session = Session.getDefaultInstance(prop, au);
//			} else {
//				session = Session.getDefaultInstance(prop);
//			}
//
//			MimeMessage msg = new MimeMessage(session);
//
//			// Subject
//			msg.setSubject(subject);
//
//			// From
//
//			msg.setFrom(new InternetAddress(from));
//
//			// To
//			for (String toAddress : toList) {
//				msg.addRecipients(Message.RecipientType.TO, toAddress);
//			}
//
//			// CC
//			if (ccList != null)
//				for (String ccAddress : ccList) {
//					msg.addRecipients(Message.RecipientType.CC, ccAddress);
//				}
//
//			// BCC
//			if (bccList != null)
//				for (String bccAddress : bccList) {
//					msg.addRecipients(Message.RecipientType.BCC, bccAddress);
//				}
//
//			// Content
//			Multipart multipart = new MimeMultipart();
//			BodyPart messageBodyPart = new MimeBodyPart();
//			messageBodyPart.setContent(content, "text/html;charset=UTF-8");
//			multipart.addBodyPart(messageBodyPart);
//			msg.setContent(multipart);
//			/*
//			 * messageBodyPart = new MimeBodyPart(); DataSource source = new
//			 * FileDataSource(fileName); messageBodyPart.setDataHandler(new
//			 * DataHandler(source)); messageBodyPart.setFileName(fileName);
//			 * multipart.addBodyPart(messageBodyPart);
//			 */
//
//			Transport.send(msg);
//		}
//	}
}
