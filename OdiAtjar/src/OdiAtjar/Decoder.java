package OdiAtjar;

import java.io.IOException;
import com.sunopsis.dwg.DwgObject;

public class Decoder
{
    @SuppressWarnings("deprecation")
	public String decodifica(final String ppwd) throws IOException {

        return DwgObject.snpsDecypher(ppwd).toString();
    }
}