package com.longjiabo.fund.util;
@Deprecated
public class PropertiesUtil {
//
//	private static Properties props;
//
//	public static String getProp(String key) {
//		return props.getProperty(key);
//	}
//
//	public static void addProp(String key, String value) {
//		DAOUtils dao = new DAOUtils();
//		if (props.containsKey(key)) {
//			String sql = "update " + ModelUtils.getTableName(Property.class)
//					+ " set pvalue=? where pkey=?";
//			dao.execute(sql, new Object[] { value, key });
//		} else {
//			Property p = new Property();
//			p.setpKey(key);
//			p.setpValue(value);
//			dao.insert(p);
//		}
//		props.put(key, value);
//	}
//
//	public static void init() {
//		props = new Properties();
//		DAOUtils dao = new DAOUtils();
//		List<Property> list = dao.queryForObjects("select * from property",
//				null, Property.class);
//		if (list.isEmpty()) {
//			initDefault();
//		}
//		for (Property p : list) {
//			props.put(p.getpKey(), p.getpValue());
//		}
//	}
//
//	private static void initDefault() {
//		addProp("subject", "fund info");
//		addProp("test_from", "jiabo.long@oracle.com");
//		addProp("from", "jiabo.long@oracle.com");
//		addProp("test_to", "jiabo.long@oracle.com");
//		addProp("to", "jiabo.long@oracle.com");
//		addProp("test_cc", "jiabo.long@oracle.com");
//		addProp("cc", "jiabo.long@oracle.com");
//		addProp("mail.smtp.host", "beehiveonline.oracle.com");
//		addProp("interval_time", "24");
//		addProp("proxy", "true");
//		addProp("start_time", "20:00");
//		addProp("crontab", "0 30/30 20-22 ? * 2-6");
//	}
}
