package com.tohsoft.app.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Created by PhongNX on 9/18/2019.
 *
 * Service xử lý tác vụ ngầm kể cả khi app bị kill
 *
 * Note: Service này chỉ là demo
 * - Nếu app không dùng cần dùng đến service cho tác vụ chạy ngầm nào thì xóa class này đi
 * - Nếu app đã có sẵn một service chạy ngầm thì dùng luôn service đó để check quyền Auto-start manager, không cần phải thêm class này vào source code
 * ( AutoStartManagerUtil.class - method shouldShowEnableAutoStart() line 82)
 */
class BackgroundService : Service() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}