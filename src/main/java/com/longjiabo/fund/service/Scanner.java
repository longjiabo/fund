package com.longjiabo.fund.service;

import java.util.List;

import com.longjiabo.fund.model.fund.History;
import com.longjiabo.fund.model.fund.Target;

public interface Scanner {
	public List<History> scanner(Target target, Integer count);

}
