package ua.pp.dvviberbot;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

/* class for check no true signature */
public class ViberSignatureValidator {
    private final String secret;

    public ViberSignatureValidator(final String secret){
        this.secret = secret;
    }

    public boolean isSignatureValid (final String signature, final String data){
        final String calcHash = encoding(this.secret,data);
        return signature.equals(calcHash);
    }

    private String encoding (final String key, final String data){
        final byte[] bytes = Hashing.hmacSha256(key.getBytes()).hashString(data, Charsets.UTF_8).asBytes();
        return BaseEncoding.base16().lowerCase().encode(bytes);

    }
}
