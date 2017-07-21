package bl.taxi.rider.smsVerifier;

/**
 * Created by DELL on 09/07/2017.
 **/

public interface OnSmsCatchListener<T> {
    void onSmsCatch(String message);
}
