package cv.rabidanti.mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private static final String LOCAL_BASE = "http://127.0.0.1:8765";
    private static final String LOCAL_UI = LOCAL_BASE + "/ui/";
    private static final int EXPORT_REQUEST = 5901;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private LinearLayout root;
    private ProgressBar progress;
    private TextView status;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildHome();
        checkRuntimeAndOpen();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView text(String value, int sizeSp, boolean bold) {
        TextView v = new TextView(this);
        v.setText(value);
        v.setTextSize(sizeSp);
        v.setTextColor(Color.rgb(17, 24, 39));
        v.setGravity(Gravity.CENTER_HORIZONTAL);
        if (bold) v.setTypeface(v.getTypeface(), android.graphics.Typeface.BOLD);
        v.setPadding(dp(8), dp(8), dp(8), dp(8));
        return v;
    }

    private Button button(String label, View.OnClickListener action) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setOnClickListener(action);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dp(6), 0, dp(6));
        b.setLayoutParams(lp);
        return b;
    }

    private void buildHome() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(20), dp(28), dp(20), dp(20));
        root.setBackgroundColor(Color.rgb(248, 250, 252));

        root.addView(text("RABIDANTI", 30, true));
        root.addView(text("Mobile Companion • Galaxy A10", 16, false));
        root.addView(text("Motor local v5.9 via Termux • Sala protegida em 127.0.0.1", 13, false));

        progress = new ProgressBar(this);
        LinearLayout.LayoutParams plp = new LinearLayout.LayoutParams(dp(48), dp(48));
        plp.setMargins(0, dp(18), 0, dp(8));
        progress.setLayoutParams(plp);
        root.addView(progress);

        status = text("A verificar o runtime local…", 15, true);
        root.addView(status);

        root.addView(button("Abrir Sala", v -> checkRuntimeAndOpen()));
        root.addView(button("Abrir Termux", v -> openTermux()));
        root.addView(button("Exportar runtime Rabidanti v5.9", v -> exportRuntime()));
        root.addView(button("Instruções rápidas", v -> showInstructions()));

        TextView note = text("Este APK não concede permissões ao Rabidanti e não expõe a Sala na rede. O Core continua no Termux.", 12, false);
        note.setPadding(dp(8), dp(20), dp(8), dp(8));
        root.addView(note);
        setContentView(root);
    }

    private void setStatus(String msg, boolean busy) {
        runOnUiThread(() -> {
            status.setText(msg);
            progress.setVisibility(busy ? View.VISIBLE : View.GONE);
        });
    }

    private void checkRuntimeAndOpen() {
        setStatus("A verificar o runtime local…", true);
        executor.submit(() -> {
            boolean ok = false;
            HttpURLConnection c = null;
            try {
                URL u = new URL(LOCAL_BASE + "/health");
                c = (HttpURLConnection) u.openConnection();
                c.setConnectTimeout(1200);
                c.setReadTimeout(1200);
                c.setInstanceFollowRedirects(false);
                int code = c.getResponseCode();
                ok = code >= 200 && code < 500;
            } catch (Exception ignored) {
            } finally {
                if (c != null) c.disconnect();
            }
            final boolean live = ok;
            runOnUiThread(() -> {
                if (live) {
                    setStatus("Runtime local encontrado. A abrir a Sala…", false);
                    openSala();
                } else {
                    setStatus("Rabidanti ainda não está ativo no Termux.", false);
                }
            });
        });
    }

    private void openSala() {
        webView = new WebView(this);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(false);
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(false);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        if (android.os.Build.VERSION.SDK_INT >= 26) s.setSafeBrowsingEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            private boolean allowed(Uri uri) {
                String scheme = uri.getScheme();
                String host = uri.getHost();
                return "http".equalsIgnoreCase(scheme) &&
                        ("127.0.0.1".equals(host) || "localhost".equalsIgnoreCase(host));
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (allowed(uri)) return false;
                return true;
            }
            @Override
            @SuppressWarnings("deprecation")
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                if (allowed(uri)) return false;
                return true;
            }
        });
        webView.loadUrl(LOCAL_UI);
        setContentView(webView);
    }

    private void openTermux() {
        Intent launch = getPackageManager().getLaunchIntentForPackage("com.termux");
        if (launch != null) {
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launch);
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Termux não encontrado")
                .setMessage("Instala o Termux e executa o setup do Rabidanti v5.9. O APK não instala software externo automaticamente.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void exportRuntime() {
        Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("application/zip");
        i.putExtra(Intent.EXTRA_TITLE, "rabidanti-v5.9-android-mobile-runtime.zip");
        startActivityForResult(i, EXPORT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != EXPORT_REQUEST || resultCode != RESULT_OK || data == null || data.getData() == null) return;
        Uri target = data.getData();
        executor.submit(() -> {
            try (InputStream in = getAssets().open("rabidanti-v5.9-android-mobile-runtime.zip");
                 java.io.OutputStream out = getContentResolver().openOutputStream(target, "w")) {
                if (out == null) throw new IOException("Não foi possível abrir o destino");
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
                out.flush();
                setStatus("Runtime v5.9 exportado com sucesso.", false);
            } catch (Exception e) {
                setStatus("Falha ao exportar: " + e.getMessage(), false);
            }
        });
    }

    private void showInstructions() {
        new AlertDialog.Builder(this)
                .setTitle("Configuração no Galaxy A10")
                .setMessage("1. Exporta o runtime v5.9 pelo botão.\n\n" +
                        "2. Abre o Termux e extrai o ZIP.\n\n" +
                        "3. Na pasta rabidanti-v5.9 executa:\n" +
                        "chmod +x SETUP-ANDROID-TERMUX.sh START-RABIDANTI-ANDROID.sh\n" +
                        "./SETUP-ANDROID-TERMUX.sh\n" +
                        "./START-RABIDANTI-ANDROID.sh\n\n" +
                        "4. Volta a este APK e toca Abrir Sala.\n\n" +
                        "A Sala fica apenas em 127.0.0.1:8765.")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else if (webView != null) {
            webView.destroy();
            webView = null;
            buildHome();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) webView.destroy();
        executor.shutdownNow();
        super.onDestroy();
    }
}
