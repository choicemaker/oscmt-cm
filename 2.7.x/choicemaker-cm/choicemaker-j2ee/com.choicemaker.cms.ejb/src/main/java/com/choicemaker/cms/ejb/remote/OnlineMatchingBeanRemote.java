package com.choicemaker.cms.ejb.remote;

import java.io.Serializable;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.choicemaker.cms.api.remote.OnlineMatchingRemote;
import com.choicemaker.cms.ejb.OnlineMatchingBean;

@Stateless
@Remote(OnlineMatchingRemote.class)
public class OnlineMatchingBeanRemote<T extends Comparable<T> & Serializable>
		extends OnlineMatchingBean<T> implements OnlineMatchingRemote<T> {
}
