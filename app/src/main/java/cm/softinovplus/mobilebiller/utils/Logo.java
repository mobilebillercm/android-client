package cm.softinovplus.mobilebiller.utils;

import android.net.Uri;

/**
 * Created by nkalla on 12/12/18.
 */

public class Logo {
    private String name;
    private Uri uri;

    public Logo(String name, Uri uri) {
        this.name = name;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
