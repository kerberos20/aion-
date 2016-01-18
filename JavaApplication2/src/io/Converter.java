package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Converter {

	public boolean Read(InputStream input, OutputStream output) throws IOException;
}
