from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.widget import Widget
from kivy.garden.androidbrowser import AndroidBrowser
from jnius import autoclass, cast
import os

# Java classes untuk Android
PythonActivity = autoclass('org.kivy.android.PythonActivity')
WebView = autoclass('android.webkit.WebView')
WebViewClient = autoclass('android.webkit.WebViewClient')
WebChromeClient = autoclass('android.webkit.WebChromeClient')
PermissionChecker = autoclass('androidx.core.content.PermissionChecker')
ContextCompat = autoclass('androidx.core.content.ContextCompat')
Intent = autoclass('android.content.Intent')

class QRScanApp(App):
    def build(self):
        self.title = 'ADEK HARIANTO - QR Scan'
        
        layout = BoxLayout(orientation='vertical')
        
        # Inisialisasi WebView
        activity = PythonActivity.mActivity
        webview = WebView(activity)
        
        # Setting WebView
        settings = webview.getSettings()
        settings.setJavaScriptEnabled(True)
        settings.setDomStorageEnabled(True)
        settings.setDatabaseEnabled(True)
        settings.setMediaPlaybackRequiresUserGesture(False)
        
        # Load HTML dari assets
        assets_path = os.path.join(os.path.dirname(__file__), 'assets', 'index.html')
        webview.loadUrl('file://' + assets_path)
        
        # Inject AndroidBridge untuk akses fitur native
        webview.addJavascriptInterface(
            WebViewBridge(activity),
            'AndroidBridge'
        )
        
        layout.add_widget(webview)
        return layout

class WebViewBridge:
    def __init__(self, activity):
        self.activity = activity
    
    def saveXlsxBase64(self, base64_data, filename):
        """Simpan file XLSX ke folder Download dari base64"""
        try:
            from base64 import b64decode
            import subprocess
            
            # Decode base64
            binary_data = b64decode(base64_data)
            
            # Tentukan path Download folder
            downloads_path = '/storage/emulated/0/Download/'
            file_path = downloads_path + filename
            
            # Simpan file
            with open(file_path, 'wb') as f:
                f.write(binary_data)
            
            return True
        except Exception as e:
            print(f"Error saving file: {e}")
            return False

if __name__ == '__main__':
    QRScanApp().run()
