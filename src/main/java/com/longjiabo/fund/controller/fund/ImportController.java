package com.longjiabo.fund.controller.fund;

public class ImportController {
//	private static final Logger log = LoggerFactory.getLogger(ImportController.class);
//
//	@Autowired
//	private DAOUtils dao;
//
//	@RequestMapping("/import")
//	public String importHome() {
//		return "import";
//	}
//
//	@RequestMapping("/import/target")
//	public void importTarget(HttpServletRequest request) {
//		List<FileItem> files = UploadUtils.getFiles(request);
//		if (files == null || files.size() > 1) {
//			return;
//		}
//		FileItem file = files.get(0);
//		try {
//			InputStream stream = file.getInputStream();
//			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
//			String line = null;
//			Target target = null;
//			while ((line = br.readLine()) != null) {
//				if (line == null || "".equals(line.trim()))
//					continue;
//				if (line.matches("[0-9]*")) {
//					target = new Target();
//					target.setCode(line);
//					target.setCreatedOn(new Date());
//				} else {
//					target.setName(line);
//					List<Target> olds = dao.queryForObjects("where code=?", new Object[] { target.getCode() },
//							Target.class);
//					if (!olds.isEmpty()) {
//						dao.execute("delete from " + ModelUtils.getTableName(Target.class) + " where code=?",
//								new Object[] { target.getCode() });
//					}
//					dao.insert(target);
//					Transaction txn = new Transaction();
//					txn.setAmount((double) 100);
//					txn.setCode(target.getCode());
//					Calendar cal = Calendar.getInstance();
//					txn.setCreatedOn(cal.getTime());
//					cal.set(Calendar.HOUR_OF_DAY, 0);
//					cal.set(Calendar.MINUTE, 0);
//					cal.set(Calendar.SECOND, 0);
//					cal.set(Calendar.MILLISECOND, 0);
//					txn.setTxnDate(cal.getTime());
//					txn.setType(Constant.TYPE_BUY);
//					txn.setGroupId(5);
//					BeanFactory.getBean(TransactionService.class).newOperation(txn);
//				}
//			}
//		} catch (IOException e) {
//			log.error("", e);
//		}
//
//	}
}
