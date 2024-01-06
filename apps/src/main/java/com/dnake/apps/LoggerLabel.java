package com.dnake.apps;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.dnake.logger.TextLogger;
import com.dnake.v700.apps;
import com.dnake.widget.Button2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TableRow;
import android.widget.TextView;

@SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
public class LoggerLabel extends BaseLabel {
	private static int MAX = 6;

	private TextView idx[] = new TextView[MAX];
	private TextView data[] = new TextView[MAX];
	private TextView ts[] = new TextView[MAX];
	private TableRow row[] = new TableRow[MAX];

	private int logger_idx, logger_max, logger_sel;

	public Context ctx = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logger);

		ctx = this;

		Button2 b = (Button2) this.findViewById(R.id.logger_btn_del);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (logger_sel != -1) {
					TextLogger.remove(logger_idx+logger_sel);
					loadData();
				}
			}
		});

		b = (Button2) this.findViewById(R.id.logger_btn_up);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				logger_idx -= MAX;
				if (logger_idx < 0)
					logger_idx = 0;
				loadData();
			}
		});

		b = (Button2) this.findViewById(R.id.logger_btn_down);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (logger_idx+MAX < TextLogger.logger.size())
					logger_idx += MAX;
				loadData();
			}
		});

		for(int i=0; i<MAX; i++) {
			row[i] = (TableRow)this.findViewById(R.id.logger_data_0+i);
			row[i].setOnClickListener(new RowOnClickListener());

			idx[i] = (TextView)this.findViewById(R.id.logger_data_idx_0+i);
			data[i] = (TextView)this.findViewById(R.id.logger_data_data_0+i);
			ts[i] = (TextView)this.findViewById(R.id.logger_data_ts_0+i);
		}
		logger_idx = 0;
		loadData();
	}

	@Override
	public void onResume() {
		super.onResume();
		loadData();
	}

	@Override
	public void onStart() {
		super.onStart();
		apps.notifyCancel();
	}

	@Override
	public void onStop() {
		super.onStop();
		if (textAlert != null) {
			textAlert.dismiss();
			textAlert = null;
		}
	}

	private void loadData() {
		if (logger_idx+MAX > TextLogger.logger.size())
			logger_max = TextLogger.logger.size()-logger_idx;
		else
			logger_max = MAX;
		for(int i=0; i<MAX; i++) {
			idx[i].setText("");
			data[i].setText("");
			ts[i].setText("");
		}
		for(int i=0; i<logger_max; i++) {
			TextLogger.data d = TextLogger.logger.get(logger_idx+i);

			if (d.iRead != 0) {
				idx[i].setTextColor(Color.GRAY);
				data[i].setTextColor(Color.GRAY);
				ts[i].setTextColor(Color.GRAY);
			} else {
				idx[i].setTextColor(Color.WHITE);
				data[i].setTextColor(Color.WHITE);
				ts[i].setTextColor(Color.WHITE);
			}

			String s = String.format("%d", logger_idx+i+1);
			idx[i].setText(s);

			data[i].setText(d.text);

			SimpleDateFormat fmt= new SimpleDateFormat("yy-MM-dd HH:mm");
			Date dt = new Date(d.date);
			ts[i].setText(fmt.format(dt));
		}
		logger_sel = -1;
	}

	private AlertDialog textAlert = null;

	private final class RowOnClickListener implements View.OnClickListener {
		@Override  
        public void onClick(View v) {
			for(int i=0; i<MAX; i++) {
				if (logger_idx+i<TextLogger.logger.size()) {
					TextLogger.data d = TextLogger.logger.get(logger_idx+i);

					if (row[i].getId() == v.getId()) {
						if (logger_sel == i) {
							TextLogger.setRead(logger_idx+i);

							if (textAlert != null) {
								textAlert.dismiss();
								textAlert = null;
							}

							LayoutInflater inflater = getLayoutInflater();
							View layout = inflater.inflate(R.layout.logger_view, (ViewGroup)findViewById(R.id.logger_view));
							TextView t = (TextView)layout.findViewById(R.id.logger_view_text);
							t.setText(d.text);

							Builder b = new AlertDialog.Builder(ctx);
							b.setTitle(R.string.logger_view_title);
							b.setView(layout);
							b.setPositiveButton(R.string.logger_view_ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									textAlert = null;
								}
							});

							textAlert = b.create();
							textAlert.setCanceledOnTouchOutside(false);
							textAlert.show();
						}
						logger_sel = i;

						idx[i].setTextColor(Color.BLUE);
						data[i].setTextColor(Color.BLUE);
						ts[i].setTextColor(Color.BLUE);
					} else {
						if (d.iRead != 0) {
							idx[i].setTextColor(Color.GRAY);
							data[i].setTextColor(Color.GRAY);
							ts[i].setTextColor(Color.GRAY);
						} else {
							idx[i].setTextColor(Color.WHITE);
							data[i].setTextColor(Color.WHITE);
							ts[i].setTextColor(Color.WHITE);
						}
					}
				}
			}
		}
	}
}
