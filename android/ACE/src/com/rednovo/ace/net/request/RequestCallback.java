/*
 * *
 *  *
 *  * @version 1.0.0
 *  *
 *  * Copyright (C) 2012-2016 REDNOVO Corporation.
 *
 */

package com.rednovo.ace.net.request;

import com.rednovo.ace.net.parser.BaseResult;

/**
 * @author Zhen.Li
 * @fileNmae RequestCallback
 * @since 2016-03-05
 */
public interface RequestCallback<T extends BaseResult> {

    void onRequestSuccess(T object);

    void onRequestFailure(T error);
}
