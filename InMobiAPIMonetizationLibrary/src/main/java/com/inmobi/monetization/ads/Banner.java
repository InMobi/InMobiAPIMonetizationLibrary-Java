package main.java.com.inmobi.monetization.ads;

import java.io.InputStream;
import java.util.ArrayList;

import main.java.com.inmobi.monetization.ads.listener.AdFormatListener;
import main.java.com.inmobi.monetization.api.net.ErrorCode;
import main.java.com.inmobi.monetization.api.request.ad.Request;
import main.java.com.inmobi.monetization.api.request.enums.AdRequest;
import main.java.com.inmobi.monetization.api.response.ad.BannerResponse;
import main.java.com.inmobi.monetization.api.response.parser.SAXBannerResponseParser;

/**
 * Publishers can use this class instance to request banner ads from InMobi.
 * 
 * @note Please pass the mandatory request params.
 * @author rishabhchowdhary
 * 
 */
public class Banner extends AdFormat {

	private SAXBannerResponseParser xmlParser = new SAXBannerResponseParser();

	public Banner() {
		requestType = AdRequest.BANNER;
	}

	/**
	 * This method is internally called from sync/async loadRequest.
	 * 
	 * @param request
	 *            The Request object
	 * @param requestType
	 *            One of Banner,Interstitial or Native ad-format
	 * @return
	 */
	protected ArrayList<BannerResponse> loadSyncRequestInternal(
			Request request, AdRequest requestType) {
		ArrayList<BannerResponse> ads = null;
		errorCode = null;
		request.setRequestType(requestType);
		InputStream is = manager.fetchAdResponseAsStream(request);
		errorCode = manager.getErrorCode();
		ads = xmlParser.fetchBannerAdsFromResponse(is, false);
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
	 * This function loads banner ads synchronously.
	 * 
	 * @param request
	 *            The Request object, containing the required request params.
	 * @NotNull request
	 * @note Please check for isRequestInProgress to false, before calling this
	 *       function.<br/>
	 *       The function returns null if the request was already in progress.
	 *       Please also provide a valid IMAdRequestObject. You may check if the
	 *       IMAdRequest object is valid by calling isValid() on the object.
	 * @return ArrayList containing the IMBannerResponse objects.
	 */

	public synchronized ArrayList<BannerResponse> loadSyncRequest(
			Request request) {

		ArrayList<BannerResponse> ads = null;
		if (canLoadRequest(request, requestType) == true) {
			ads = loadSyncRequestInternal(request, requestType);
		}

		return ads;
	}

	/**
	 * This method fires the ad request in a new thread.
	 * 
	 * @note You must implement a listener to get success/failure connection
	 *       callbacks. Please refer the loadSyncBannerAd() description for
	 *       details.
	 */
	public void loadAsyncRequest(final Request request,
			final AdFormatListener listener) {
		final Banner bannerAd = this;
		if (request == null) {
			return;
		}
		if (listener == null) {
			return;
		}
		(new Thread() {
			public void run() {
				ArrayList<BannerResponse> ads = null;
				if (canLoadRequest(request, requestType) == true) {
					ads = loadSyncRequestInternal(request, requestType);
					if (ads != null && ads.size() > 0) {
						listener.onSuccess(bannerAd, ads);
					} else {
						listener.onFailure(bannerAd, errorCode);
					}
				}

			}
		}).start();
	}
}
