package fr.gouv.vitam.tools.resip.sedaobjecteditor.components.viewers;

import fr.gouv.vitam.tools.resip.data.StatisticData;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * The type Statistic table model.
 */
public class StatisticTableModel extends AbstractTableModel {

    private final String[] entetes = { "Catégorie", "Nombre", "Taille min", "Taille moy", "Taille max", "Total"};
    private List<StatisticData> statisticDataList;

    /**
     * Gets statistic data list.
     *
     * @return the statistic data list
     */
    public List<StatisticData> getStatisticDataList() {
        return statisticDataList;
    }

    /**
     * Sets statistic data list.
     *
     * @param statisticDataList the statistic data list
     */
    public void setStatisticDataList(List<StatisticData> statisticDataList) {
        this.statisticDataList = statisticDataList;
    }

    @Override
    public int getColumnCount() {
        return entetes.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return entetes[columnIndex];
    }

    @Override
    public Class getColumnClass(int col) {
        if (col >0)       //second column accepts only Integer values
            return Long.class;
        else return String.class;  //other columns accept String values
    }

    @Override
    public int getRowCount() {
        if (statisticDataList == null) return 0;
        return statisticDataList.size();
    }

    @Override
    public Object getValueAt(int arg0, int arg1) {
        if (statisticDataList == null) return null;
        if (arg0 >= statisticDataList.size())
            throw new IllegalArgumentException();
        StatisticData statisticData = statisticDataList.get(arg0);
        switch (arg1) {
            case 0:
                return statisticData.getFormatCategory();
            case 1:
                return statisticData.getObjectNumber();
            case 2:
                if (statisticData.getObjectNumber() == 0)
                    return Long.MAX_VALUE;
                return statisticData.getMinSize();
            case 3:
                if (statisticData.getObjectNumber() == 0)
                    return Long.MAX_VALUE;
                return Math.round(statisticData.getMeanSize());
            case 4:
                if (statisticData.getObjectNumber() == 0)
                    return Long.MAX_VALUE;
                return statisticData.getMaxSize();
            case 5:
                if (statisticData.getObjectNumber() == 0)
                    return Long.MAX_VALUE;
                return statisticData.getTotalSize();
            default:
                throw new IllegalArgumentException();
        }
    }
}