package com.crm.docs.common.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

//TODO : 추후에는 데이터베이스로 옮겨야 함 - value는 k8s의 svc
@Component
public class DeviceServiceMapper {

	private static final Map<String, String> DEVICE_TO_SERVICE = new ConcurrentHashMap<>();

	@PostConstruct
	public void initialize() {
		// Android devices - Galaxy S23
		DEVICE_TO_SERVICE.put("android:galaxy_s23:11", "android-emulator-service");
		DEVICE_TO_SERVICE.put("android:galaxy_s23:12", "android-emulator-service");
		DEVICE_TO_SERVICE.put("android:galaxy_s23:13", "android-emulator-service");
		DEVICE_TO_SERVICE.put("android:galaxy_s23:14", "android-emulator-service");

		// Android devices - Galaxy S24
		DEVICE_TO_SERVICE.put("android:galaxy_s24:11", "android-emulator-service");
		DEVICE_TO_SERVICE.put("android:galaxy_s24:12", "android-emulator-service");
		DEVICE_TO_SERVICE.put("android:galaxy_s24:13", "android-emulator-service");
		DEVICE_TO_SERVICE.put("android:galaxy_s24:14", "android-emulator-service");

		// Android devices - Galaxy Z Fold 5
		DEVICE_TO_SERVICE.put("android:galaxy_z_fold5:11", "android-emulator-service");
		DEVICE_TO_SERVICE.put("android:galaxy_z_fold5:12", "android-emulator-service");
		DEVICE_TO_SERVICE.put("android:galaxy_z_fold5:13", "android-emulator-service");
		DEVICE_TO_SERVICE.put("android:galaxy_z_fold5:14", "android-emulator-service");

		// Android devices - Pixel 7
		DEVICE_TO_SERVICE.put("android:pixel_7:11", "android-emulator-service");
		DEVICE_TO_SERVICE.put("android:pixel_7:12", "android-emulator-service");
		DEVICE_TO_SERVICE.put("android:pixel_7:13", "android-emulator-service");
		DEVICE_TO_SERVICE.put("android:pixel_7:14", "android-emulator-service");

		// Android devices - Pixel 8
		DEVICE_TO_SERVICE.put("android:pixel_8:11", "android-emulator-service");
		DEVICE_TO_SERVICE.put("android:pixel_8:12", "android-emulator-service");
		DEVICE_TO_SERVICE.put("android:pixel_8:13", "android-emulator-service");
		DEVICE_TO_SERVICE.put("android:pixel_8:14", "android-emulator-service");

		// iOS devices - iPhone 13
		DEVICE_TO_SERVICE.put("ios:iphone_13:15", "android-emulator-service");
		DEVICE_TO_SERVICE.put("ios:iphone_13:16", "android-emulator-service");
		DEVICE_TO_SERVICE.put("ios:iphone_13:17", "android-emulator-service");
		DEVICE_TO_SERVICE.put("ios:iphone_13:18", "android-emulator-service");

		// iOS devices - iPhone 14
		DEVICE_TO_SERVICE.put("ios:iphone_14:15", "android-emulator-service");
		DEVICE_TO_SERVICE.put("ios:iphone_14:16", "android-emulator-service");
		DEVICE_TO_SERVICE.put("ios:iphone_14:17", "android-emulator-service");
		DEVICE_TO_SERVICE.put("ios:iphone_14:18", "android-emulator-service");

		// iOS devices - iPhone 15
		DEVICE_TO_SERVICE.put("ios:iphone_15:15", "android-emulator-service");
		DEVICE_TO_SERVICE.put("ios:iphone_15:16", "android-emulator-service");
		DEVICE_TO_SERVICE.put("ios:iphone_15:17", "android-emulator-service");
		DEVICE_TO_SERVICE.put("ios:iphone_15:18", "android-emulator-service");

		// iOS devices - iPhone 15 Pro
		DEVICE_TO_SERVICE.put("ios:iphone_15_pro:15", "android-emulator-service");
		DEVICE_TO_SERVICE.put("ios:iphone_15_pro:16", "android-emulator-service");
		DEVICE_TO_SERVICE.put("ios:iphone_15_pro:17", "android-emulator-service");
		DEVICE_TO_SERVICE.put("ios:iphone_15_pro:18", "android-emulator-service");

		// iOS devices - iPhone 15 Pro Max
		DEVICE_TO_SERVICE.put("ios:iphone_15_pro_max:15", "android-emulator-service");
		DEVICE_TO_SERVICE.put("ios:iphone_15_pro_max:16", "android-emulator-service");
		DEVICE_TO_SERVICE.put("ios:iphone_15_pro_max:17", "android-emulator-service");
		DEVICE_TO_SERVICE.put("ios:iphone_15_pro_max:18", "android-emulator-service");
	}

	public String getServiceName(String os, String device, String osVersion) {
		String key = String.format("%s:%s:%s", os, device, osVersion);
		return DEVICE_TO_SERVICE.getOrDefault(key, "default-svc");
	}

}