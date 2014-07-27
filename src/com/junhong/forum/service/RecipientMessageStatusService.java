package com.junhong.forum.service;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import com.junhong.message.domain.RecipientMessageStatus;

@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.CONTAINER)
public class RecipientMessageStatusService extends GenericCRUDService<RecipientMessageStatus> {

}
