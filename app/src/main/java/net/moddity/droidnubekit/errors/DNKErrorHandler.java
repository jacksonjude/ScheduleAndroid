package net.moddity.droidnubekit.errors;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.moddity.droidnubekit.DroidNubeKit;
import net.moddity.droidnubekit.responsemodels.DNKRecordsResponse;
import net.moddity.droidnubekit.responsemodels.DNKUnauthorizedResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.mime.MimeUtil;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

/**
 * Created by jaume on 12/6/15.
 */
public class DNKErrorHandler implements ErrorHandler {
    @Override
    public Throwable handleError(RetrofitError cause) {

        if(cause.getResponse() == null) //No connection¿? //todo improve this
            return createException(cause);

        switch (cause.getResponse().getStatus()) {
            case DNKErrorCodes.AUTHENTICATION_REQUIRED:
                DNKUnauthorizedResponse errorResponse = (DNKUnauthorizedResponse)cause.getBodyAs(DNKUnauthorizedResponse.class);

                DroidNubeKit.showAuthDialog(errorResponse.getRedirectURL());

                return new DNKAuthenticationRequiredException(DNKErrorCodes.AUTHENTICATION_REQUIRED, cause.getCause(), errorResponse);

            default:
                return createException(cause);
        }
    }

    private DNKException createException(RetrofitError cause) {
        if (cause.getResponse() != null)
        {
            DNKException exception = new DNKException(cause.getResponse().getStatus(), cause.getCause());
            return exception;
        }
        else
        {
            DNKException exception = new DNKException(-1, cause.getCause());
            return exception;
        }
    }

    private String getResponseBodyAsString(RetrofitError cause)
    {
        TypedInput body = cause.getResponse().getBody();

        if (body!= null) {

            if (!(body instanceof TypedByteArray)) {
                // Read the entire response body to we can log it and replace the original response
                body = cause.getResponse().getBody();
            }

            byte[] bodyBytes = ((TypedByteArray) body).getBytes();
            String bodyMime = body.mimeType();
            String bodyCharset = MimeUtil.parseCharset(bodyMime);

            try
            {
                System.out.println(new String(bodyBytes, bodyCharset));
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }
}
