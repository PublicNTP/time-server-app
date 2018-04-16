package org.publicntp.gnssreader.ui.custom;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.helper.GreyLevelHelper;
import org.publicntp.gnssreader.model.SatelliteModel;
import org.publicntp.gnssreader.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by zac on 4/3/18.
 */

public class SignalGraphFragment extends BaseFragment {
    private List<SatelliteModel> satelliteModelList;
    private OnSatelliteSelectedListener onSatelliteSelectedListener;

    @BindView(R.id.satellite_bar_chart) ColumnChartView signalGraph;
    @BindView(R.id.signal_graph_scroll_container) HorizontalScrollView horizontalScrollView;


    public static SignalGraphFragment newInstance(List<SatelliteModel> satelliteModelList, @NonNull OnSatelliteSelectedListener onSatelliteSelectedListener) {
        SignalGraphFragment signalGraphFragment = new SignalGraphFragment();
        signalGraphFragment.setSatelliteModels(satelliteModelList, false);
        signalGraphFragment.setOnSatelliteSelectedListener(onSatelliteSelectedListener);
        return signalGraphFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_signal_graph, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    private void setSatelliteModels(List<SatelliteModel> satelliteModelList, boolean setUI) {
        this.satelliteModelList = satelliteModelList;
        if (setUI) setBarChartData(satelliteModelList);
    }

    public void setSatelliteModels(List<SatelliteModel> satelliteModelList) {
        setSatelliteModels(satelliteModelList, true);
    }

    private void setBarChartData(List<SatelliteModel> satellites) {
        if (getActivity() == null) {
            return; //Fragment is not visible, and cannot update.
        }

        List<Column> satelliteSignalValues = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();
        int i = 0;
        for (SatelliteModel s : satellites) {
            SubcolumnValue subcolumnValue = new SubcolumnValue(s.Cn0DbHz).setColor(GreyLevelHelper.asColor(getContext(), s.Cn0DbHz));
            List<SubcolumnValue> subcolumnValues = new ArrayList<>();
            subcolumnValues.add(subcolumnValue);

            Column column = new Column().setValues(subcolumnValues);
            AxisValue axisValue = new AxisValue(i).setLabel(s.svn + "");
            i++;

            satelliteSignalValues.add(column);
            axisValues.add(axisValue);
        }
        ColumnChartData columnChartData = new ColumnChartData(satelliteSignalValues);

        Axis xAxis = new Axis();
        xAxis.setValues(axisValues);
        xAxis.setMaxLabelChars(3);
        xAxis.setInside(true);

        columnChartData.setAxisXBottom(xAxis);
        signalGraph.setColumnChartData(columnChartData);
        signalGraph.setZoomEnabled(false);
        signalGraph.setInteractive(true);
        signalGraph.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

        signalGraph.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                SatelliteModel selectedSatellite = satelliteModelList.get(columnIndex);
                onSatelliteSelectedListener.onSatelliteSelected(selectedSatellite);
            }

            @Override
            public void onValueDeselected() {

            }
        });

        ViewGroup.LayoutParams layoutParams = signalGraph.getLayoutParams();
        layoutParams.width = Math.max(satelliteSignalValues.size() * 200, horizontalScrollView.getMeasuredWidth());
        layoutParams.height = horizontalScrollView.getMeasuredHeight();
        signalGraph.setLayoutParams(layoutParams);
    }

    public void setOnSatelliteSelectedListener(OnSatelliteSelectedListener onSatelliteSelectedListener) {
        this.onSatelliteSelectedListener = onSatelliteSelectedListener;
    }

    public interface OnSatelliteSelectedListener {
        void onSatelliteSelected(SatelliteModel satelliteModel);
    }
}
