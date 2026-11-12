package com.olimposystems.android.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Insets;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

/**
 * A partir do Android 15 (API 35) o app é desenhado de ponta a ponta, por baixo das barras
 * do sistema. Aplica as margens das barras/recorte como padding no conteúdo da tela.
 * Chamar depois de setContentView().
 */
public final class EdgeToEdge {

	private EdgeToEdge() {
	}

	public static void aplica(Activity activity) {
		if (Build.VERSION.SDK_INT < 35) {
			return;
		}
		aplicaInsets(activity);
	}

	@TargetApi(35)
	private static void aplicaInsets(Activity activity) {
		ViewGroup content = activity.findViewById(android.R.id.content);
		final View raiz = content.getChildAt(0);
		if (raiz == null) {
			return;
		}
		raiz.setOnApplyWindowInsetsListener((v, insets) -> {
			Insets barras = insets.getInsets(
					WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
			v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
			return WindowInsets.CONSUMED;
		});
		raiz.requestApplyInsets();
	}
}
