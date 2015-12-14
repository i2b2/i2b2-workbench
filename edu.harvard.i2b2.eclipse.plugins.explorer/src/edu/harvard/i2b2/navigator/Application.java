/*
* Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *     
 */
package edu.harvard.i2b2.navigator;

public class Application {

    private String valType;
    private String name;
    private boolean encrypted;
    private String className;
    private String command;
    private String arguments;
    private String workingDirectory;

    public String getWorkingDirectory() {
	return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
	this.workingDirectory = workingDirectory;
    }

    public String getArguments() {
	return arguments;
    }

    public void setArguments(String arguments) {
	this.arguments = arguments;
    }

    public String getClassName() {
	return className;
    }

    public void setClassName(String className) {
	this.className = className;
    }

    public String getCommand() {
	return command;
    }

    public void setCommand(String command) {
	this.command = command;
    }

    public boolean getEncrypted() {
	return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
	this.encrypted = encrypted;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getValType() {
	return valType;
    }

    public void setValType(String valType) {
	this.valType = valType;
    }

}
