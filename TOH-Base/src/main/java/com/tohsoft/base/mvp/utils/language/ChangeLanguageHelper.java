package com.tohsoft.base.mvp.utils.language;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tohsoft.ads.AdsModule;
import com.tohsoft.base.mvp.R;
import com.utility.DebugLog;
import com.utility.SharedPreference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ChangeLanguageHelper {
    private static final String URL_GET_COUNTRY_CODE_BY_IP = "http://gsp1.apple.com/pep/gcc";
    private static final String DEFAULT_COUNTRY_CODE = "US";
    private static final String DEFAULT_LANGUAGE = "en";
    public static final String RESTART_APP_TO_APPLY_LANGUAGE_CHANGED = "RESTART_APP_TO_APPLY_LANGUAGE_CHANGED";
    private final Context mContext;
    @Nullable
    private ChangeLanguageListener mListener;

    public interface ChangeLanguageListener {
        void onChangeLanguageSuccess();
    }

    public ChangeLanguageHelper(Context context, @Nullable ChangeLanguageListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    @SuppressLint("CheckResult")
    public void changeLanguage(Class mainActivity) {
        Observable.concat(getCountryBySim(), getCountryByIp())
                .filter(s -> !TextUtils.isEmpty(s))
                .first(DEFAULT_COUNTRY_CODE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(countryCode -> {
                    showDialog(countryCode, mainActivity);
                }, throwable -> {
                    showDialog(DEFAULT_COUNTRY_CODE, mainActivity);
                });
    }

    private void showDialog(String countryCode, Class mainActivity) {
        String[] key_languages = mContext.getResources().getStringArray(R.array.key_language_support);
        List<String> languages = new ArrayList<>();
        Locale loc;
        String selected = mContext.getString(R.string.lbl_auto);
        String detectedLanguage = getLanguageFromCountry(mContext, countryCode);
        boolean hasDetectLanguage = false;
        for (String key : key_languages) {
            String[] spk = key.split("-");
            if (spk.length > 1) {
                loc = new Locale(spk[0], spk[1]);
            } else {
                loc = new Locale(key);
            }
            if (key.equalsIgnoreCase(LocaleManager.getLanguage(mContext))) {
                selected = loc.getDisplayName(loc);
            }
            if (key.equalsIgnoreCase(DEFAULT_LANGUAGE)) {
                continue;
            }
            if (key.equalsIgnoreCase(detectedLanguage)) {
                hasDetectLanguage = true;
                continue;
            }
            languages.add(toDisplayCase(loc.getDisplayName(loc)));
        }
        Collections.sort(languages);

        if (!DEFAULT_LANGUAGE.equalsIgnoreCase(detectedLanguage) && hasDetectLanguage) {
            languages.add(0, toDisplayCase(new Locale(detectedLanguage).getDisplayName(new Locale(detectedLanguage))));
        }
        languages.add(0, toDisplayCase(new Locale(DEFAULT_LANGUAGE).getDisplayName(new Locale(DEFAULT_LANGUAGE))));

        languages.add(0, toDisplayCase(mContext.getString(R.string.lbl_auto)));
        int pos = 0;
        for (int i = 0; i < languages.size(); i++) {
            if (languages.get(i).equalsIgnoreCase(selected)) {
                pos = i;
                break;
            }
        }

        final int selectedPos = pos;
        LocaleManager.getLanguage(mContext);

        showDialogSelectLanguage(mContext, languages, selectedPos, (dialog, which) -> {
            ItemLanguageAdapter adapter = null;
            if (dialog.getRecyclerView() != null && dialog.getRecyclerView().getAdapter() instanceof ItemLanguageAdapter) {
                adapter = (ItemLanguageAdapter) dialog.getRecyclerView().getAdapter();
            }
            if (adapter != null && adapter.getSelectedIndex() != selectedPos) {
                if (adapter.getSelectedIndex() == 0) {
                    LocaleManager.setNewLocale(mContext, LocaleManager.MODE_AUTO);
                    restartToApplyLanguage(mainActivity);
                    return;
                }
                String selectedLang = languages.get(adapter.getSelectedIndex());
                for (String key : key_languages) {
                    Locale lloc;
                    String[] spk = key.split("-");
                    if (spk.length > 1) {
                        lloc = new Locale(spk[0], spk[1]);
                    } else {
                        lloc = new Locale(key);
                    }
                    if (selectedLang.equalsIgnoreCase(lloc.getDisplayName(lloc))) {
                        LocaleManager.setNewLocale(mContext, key);
                        restartToApplyLanguage(mainActivity);
                        break;
                    }
                }
            }
        });
    }

    private void showDialogSelectLanguage(Context context, List<String> languages, int selectedPos, MaterialDialog.SingleButtonCallback callbackDone) {
        new MaterialDialog.Builder(context)
                .title(R.string.lbl_select_language)
                .adapter(new ItemLanguageAdapter(languages, selectedPos), new LinearLayoutManager(context))
                .positiveText(R.string.action_done)
                .onPositive(callbackDone)
                .build()
                .show();
    }

    private void restartToApplyLanguage(Class mainActivity) {
        if (mListener != null) {
            mListener.onChangeLanguageSuccess();
        }
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .content(R.string.msg_restart_to_change_config)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .show();

        new Handler().postDelayed(() -> {
            dialog.dismiss();

            AdsModule.getInstance().setIgnoreDestroyStaticAd(true);

            Intent intent = new Intent(mContext, mainActivity);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
        }, 3000);
    }

    private Observable<String> getCountryByIp() {
        return Observable.create(subscriber -> {
            try {
                String countryCode = getCountryCode(mContext);
                if (countryCode.isEmpty()) {
                    try {
                        String response = new NetworkCall().makeServiceCall(URL_GET_COUNTRY_CODE_BY_IP);
                        if (response != null && !response.isEmpty()) {
                            setCountryCode(mContext, response);
                        }
                        if (response != null) {
                            subscriber.onNext(response.toLowerCase());
                        }
                    } catch (Exception e) {
                        DebugLog.loge(e);
                    }
                } else {
                    subscriber.onNext(countryCode);
                }
            } catch (Exception e) {
                subscriber.onNext("");
            }
            subscriber.onComplete();
        });
    }

    private Observable<String> getCountryBySim() {
        return Observable.create(subscriber -> {
            try {
                final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
                final String simCountry;
                if (tm != null) {
                    simCountry = tm.getSimCountryIso();
                    if (simCountry != null && simCountry.length() == 2) {
                        subscriber.onNext(simCountry.toLowerCase(Locale.US));
                    } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) {
                        String networkCountry = tm.getNetworkCountryIso();
                        if (networkCountry != null && networkCountry.length() == 2) {
                            subscriber.onNext(networkCountry.toLowerCase(Locale.US));
                        }
                    }
                }
            } catch (Exception e) {
                subscriber.onNext("");
            }
            subscriber.onComplete();
        });
    }

    private String getLanguageFromCountry(Context context, String country) {
        String[] CountryCode = context.getResources().getStringArray(R.array.CountryCodes);
        List<String> languageCodeSupport = Arrays.asList(context.getResources().getStringArray(R.array.key_language_support));

        for (String s : CountryCode) {
            String[] lg = s.split("_");
            if (lg.length > 1 && lg[0].equalsIgnoreCase(country) && languageCodeSupport.contains(lg[0])) {
                return lg[0];
            }
        }
        for (String s : CountryCode) {
            String[] lg = s.split("_");
            if (lg.length > 1 && lg[1].equalsIgnoreCase(country) && languageCodeSupport.contains(lg[0])) {
                return lg[0];
            }
        }
        return null;
    }

    private String toDisplayCase(String s) {

        final String ACTIONABLE_DELIMITERS = " '-/";
        StringBuilder sb = new StringBuilder();
        boolean capNext = true;

        for (char c : s.toCharArray()) {
            c = (capNext) ? Character.toUpperCase(c) : Character.toLowerCase(c);
            sb.append(c);
            capNext = (ACTIONABLE_DELIMITERS.indexOf((int) c) >= 0);
        }
        return sb.toString();
    }

    /*
     * Country code
     * */
    private static String KEY_COUNTRY_CODE_BY_IP = "country_code_by_ip";

    private String getCountryCode(Context context) {
        return SharedPreference.getString(context, KEY_COUNTRY_CODE_BY_IP, "");
    }

    private void setCountryCode(Context context, String value) {
        SharedPreference.setString(context, KEY_COUNTRY_CODE_BY_IP, value);
    }

}
