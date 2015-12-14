/*
 * Copyright (c) 2006-2010 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   	
 *     
 */
package edu.harvard.i2b2.eclipse.plugins.adminTool.utils;

import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.common.exception.I2B2Exception; //import edu.harvard.i2b2.navigator.LoginDHelper;
import edu.harvard.i2b2.patientMapping.security.HighEncryption;

public class SecurityUtil {

	private HighEncryption notesHighEnc = null;
	private static final Log log = LogFactory.getLog(SecurityUtil.class);

	/**
	 * Default note key.
	 */
	private String notesKey = " ";

	/**
	 * Constructor to accept only notes key. Default key is used for empi and
	 * encounter encryption.
	 * 
	 * @param notesKey
	 */
	public SecurityUtil(String notesKey) {
		this.notesKey = notesKey;
		initHighEncrypt();
	}

	/**
	 * Initialize HighEncryption variable for empi and notes.
	 */
	private void initHighEncrypt() {
		try {

			// init high encryption with notes key
			Hashtable<String, String> hashNotestemp = new Hashtable<String, String>();
			hashNotestemp.put("A:\\I401.txt", notesKey);
			notesHighEnc = new HighEncryption("A:\\I401.txt", hashNotestemp);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Decrypt notes with notes key.
	 * 
	 * @param encrypted
	 *            notes
	 * @return
	 */
	public String decryptNotes(String encryptedNotes) {

		String tmp = null;

		try {
			tmp = notesHighEnc.generic_decrypt(encryptedNotes);
		} catch (I2B2Exception e) {
			if (e.getMessage().equalsIgnoreCase("Invalid key")) {
				log.error("Invalid key");
				return "[I2B2-Error] Invalid key";
			}
		}

		return tmp;
	}
	
	/**
	 * Decrypt notes with notes key.
	 * 
	 * @param encrypted
	 *            notes
	 * @return
	 */
	public String encryptNotes(String encryptedNotes) {

		String tmp = null;

		try {
			tmp = notesHighEnc.generic_encrypt(encryptedNotes);
		} catch (I2B2Exception e) {
			if (e.getMessage().equalsIgnoreCase("Invalid key")) {
				log.error("Invalid key");
				return "[I2B2-Error] Invalid key";
			}
		}

		return tmp;
	}

}
