package org.bcjj.huntfiles;

public class Hit {

 int lineNumber;
 String line;
 
 public Hit(int lineNumber, String line) {
	 this.lineNumber=lineNumber;
	 this.line=line;
 }
 
 public String toString() {
	 return "("+String.format("%1$6s", lineNumber)+"):: "+line;
 }
 
}
