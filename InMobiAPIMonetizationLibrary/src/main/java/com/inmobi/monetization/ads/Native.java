package main.java.com.inmobi.monetization.ads;

import java.util.ArrayList;

import main.java.com.inmobi.monetization.ads.listener.AdFormatListener;
import main.java.com.inmobi.monetization.api.net.ErrorCode;
import main.java.com.inmobi.monetization.api.request.ad.Request;
import main.java.com.inmobi.monetization.api.request.enums.AdRequest;
import main.java.com.inmobi.monetization.api.response.ad.NativeResponse;
import main.java.com.inmobi.monetization.api.response.parser.JSONNativeResponseParser;

/**
 * Publishers may use this class to request Native ads from InMobi.
 * 
 * @author rishabhchowdhary
 * 
 */
public class Native extends AdFormat {

	private JSONNativeResponseParser jsonParser = new JSONNativeResponseParser();

	public Native() {
		requestType = AdRequest.NATIVE;
	}
	
	private ArrayList<NativeResponse> loadSyncRequestInternal(Request request) {
		errorCode = null;
		ArrayList<NativeResponse> ads = null;
		request.setRequestType(requestType);
		String response = manager.fetchAdResponseAsString(request);
		errorCode = manager.getErrorCode();
		ads = jsonParser.fetchNativeAdsFromResponse(response);
		isRequestInProgress.set(false);
		if (ads == null) {
			errorCode = new ErrorCode(ErrorCode.NO_FILL,
					"Server returned a no-fill.");
		} else if (ads.size() == 0) {
			errorCode = new ErrorCode(ErrorCode.NO_FILL,
					"Server returned a no-fill.");
		}
		return ads;
	}

	/**
	 * This function loads native ads synchronously.
	 * 
	 * @note Please check for isRequestInProgress to false, before calling this
	 *       function.<br/>
	 *       The function returns null if the request was already in progress.
	 *       Please also provide a valid IMAdRequestObject. You may check if the
	 *       IMAdRequest object is valid by calling isValid() on the object.
	 * @return ArrayList containing the NativeResponse objects.
	 */
	public synchronized ArrayList<NativeResponse> loadSyncRequest(Request request) {
		ArrayList<NativeResponse> ads = null;
		
		if (canLoadRequest(request,requestType) == true) {
			ads = loadSyncRequestInternal(request);
		}
		return ads;
	}

	/**
	 *  This method fires the ad request in a new thread.
	 * 
	 * @note You must implement a listener to get success/failure connection
	 *       callbacks. Please refer the loadSyncNativeAd() description for
	 *       details.
	 */
	public void loadAsyncRequest(final Request request,final AdFormatListener listener) {
		final Native nativeAd = this;
		if(request == null) {
			return;
		}
		if(listener == null) {
			return;
		}
		(new Thread() {
			public void run() {
				ArrayList<NativeResponse> ads = null;
				if (canLoadRequest(request,requestType) == true) {
					ads = loadSyncRequestInternal(request);
					if (listener != null) {
						if (ads != null && ads.size() > 0) {
							listener.onSuccess(nativeAd, ads);
						} else {
							listener.onFailure(nativeAd, errorCode);
						}
					}
				}
				
			}
		}).start();
	}

}
