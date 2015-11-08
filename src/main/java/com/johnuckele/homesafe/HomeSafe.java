package com.johnuckele.homesafe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class HomeSafe {
    public static void main(String[] args) {
	System.out.println("Hello Home Safe");
	Runtime r = Runtime.getRuntime();
	try {
	    // Start process
	    Process p = r.exec(
		    "/home/juckele/.steam/steam/steamapps/common/Terraria/TerrariaServer -config server.txt");
	    InputStream pout = p.getInputStream();
	    InputStream perr = p.getErrorStream();
	    OutputStream pin = p.getOutputStream();

	    OutputReader outReader = new OutputReader(pout, pin, false);
	    outReader.start();
	    OutputReader errReader = new OutputReader(perr, pin, true);
	    errReader.start();
	    while (true) {
		Thread.sleep(1000);
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    public static class OutputReader extends Thread {
	private InputStream _standardOut;
	private OutputStream _standardIn;
	private boolean _isErrorStream;

	public OutputReader(InputStream standardOut, OutputStream standardIn,
		boolean isErrorStream) {
	    this._standardOut = standardOut;
	    this._standardIn = standardIn;
	    this._isErrorStream = isErrorStream;
	}

	@Override
	public void run() {
	    // Read
	    BufferedReader in = new BufferedReader(
		    new InputStreamReader(this._standardOut));
	    String line = null;

	    try {
		while ((line = in.readLine()) != null) {
		    if (_isErrorStream) {
			System.err.println(line);
		    } else {
			System.out.println(line);
		    }
		    if (line.contains("has left.")) {
			System.out.println("Try to save?");
			_standardIn.write(
				"\nsay Server Trying To Save\n".getBytes());
			_standardIn.flush();
			_standardIn.write("\nsave\n".getBytes());
			_standardIn.flush();
		    }
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }
}
