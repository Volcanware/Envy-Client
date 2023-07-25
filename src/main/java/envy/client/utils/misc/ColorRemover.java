package envy.client.utils.misc;

public class ColorRemover {
    public static String GetVerbatim(String text)
    {
        int idx = 0;
        var data = new char[text.length()];

        for ( int i = 0; i < text.length(); i++ )
            if ( text.charAt(i) != '§' )
                data[idx++] = text.charAt(i);
            else
                i++;

        return new String(data, 0, idx);
    }

}
