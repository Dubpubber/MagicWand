/*
 * MIT License
 *
 * Copyright (c) 2017 Tyler Crowe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.loneboat.dnd.magicwand.ui.controller;

import com.loneboat.dnd.magicwand.Spell;
import com.loneboat.dnd.magicwand.ui.MagicWand;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BaseController implements Initializable {
    // Begin linking our FXML objects for the controller's usage.
    // Left Side Misc
    @FXML private Label mw_SpellsFound;
    @FXML private GridPane mw_NarrowGrid;
    @FXML private Label mw_SpellLevel_Lbl;

    // Summary Tab
    @FXML private Label mw_SumPan_0; // Selected Spell
    @FXML private Label mw_SumPan_1; // School
    @FXML private Label mw_SumPan_2; // Unlock Level
    @FXML private Label mw_SumPan_3; // Classes
    @FXML private Label mw_SumPan_4; // Components
    @FXML private Label mw_SumPan_5; // Casting Time
    @FXML private Label mw_SumPan_6; // Range
    @FXML private Label mw_SumPan_7; // Duration
    @FXML private Label mw_SumPan_8; // Source
    // Description Tab
    @FXML private TextArea mw_SpellDescTxtArea;

    // Table links
    @FXML private TableView<Spell> mw_SpellTable;
    @FXML private TableColumn<Spell, String> mw_SpellTableColumn;

    // Focus query fields
    @FXML private TextField mw_SpellName;
    @FXML private ComboBox<String> mw_SpellSchool;
    private final int MINMAX_WIDTH = 175; // Used for check combo boxes
    private CheckComboBox classCheckComboBox;
    private CheckComboBox componentsCheckComboBox;
    @FXML private Slider mw_SpellLevel_Slider;

    // DEBUG, used for populating the table view will all spells in SQLite database.
    private final String MassNameLookup = "SELECT SpellName FROM SpellList;";
    // Other SQL statements.
    private final String FocusStart = "SELECT SpellName FROM SpellList WHERE SpellName LIKE '%'";
    private final String LikeDescLookup = "SELECT SpellSchool, SpellDesc, SpellMinLevel, SpellSupportingClasses, SpellComponents, SpellCastingTime, SpellRange, SpellDuration, SpellSource FROM SpellList WHERE SpellName LIKE ?;";

    // Observable list containing all spells currently in the table.
    private ObservableList<Spell> tableSpells;

    /**
     * This method is called on when the FXController is initialized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.tableSpells = FXCollections.observableArrayList();
        mw_SpellTableColumn.setCellValueFactory(new PropertyValueFactory<>("spellName"));
        mw_SpellSchool.setItems(FXCollections.observableArrayList(
                "All Schools", "Abjuration", "Conjuration", "Divination", "Enchantment", "Evocation", "Illusion", "Necromancy", "Transmutation", "Universal"
        ));
        mw_SpellSchool.getSelectionModel().select(0);
        // Add our ControlFX check combo boxes.
        buildComboBoxClasses();
        buildComboBoxComponents();
        populateTableView();
    }

    /**
     * Submit button for narrowing table results.
     */
    @FXML
    public void onSubmit() {
        populateTableView();
    }

    // TODO: Investigate query builders
    private boolean populateTableView() {
        Connection conn = MagicWand.openConnection();
        if(conn == null) return false;
        if(!mw_SpellName.getText().isEmpty() || !mw_SpellSchool.getSelectionModel().getSelectedItem().equalsIgnoreCase("All Schools") || !classCheckComboBox.getCheckModel().isEmpty() || !componentsCheckComboBox.getCheckModel().isEmpty() || (int) mw_SpellLevel_Slider.getValue() != 10) {
            List<Object> vars = new ArrayList<>();
            StringBuilder query = new StringBuilder(FocusStart);
            if(!mw_SpellName.getText().isEmpty()) {
                query.append(" and SpellName like ?");
                vars.add('%' + mw_SpellName.getText() + '%');
            }
            if(!mw_SpellSchool.getSelectionModel().getSelectedItem().equalsIgnoreCase("All Schools")) {
                query.append(" and SpellSchool like ?");
                vars.add('%' + String.valueOf(mw_SpellSchool.getSelectionModel().getSelectedItem()) + '%');
            }
            if((int) mw_SpellLevel_Slider.getValue() != 10) {
                query.append(" and SpellMinLevel=?");
                vars.add((int) mw_SpellLevel_Slider.getValue());
            }
            if(!classCheckComboBox.getCheckModel().isEmpty()) {
                for(Object item : classCheckComboBox.getCheckModel().getCheckedItems()) {
                    query.append(" and SpellSupportingClasses like ?");
                    vars.add('%' + String.valueOf(item) + '%');
                }
            }
            if(!componentsCheckComboBox.getCheckModel().isEmpty()) {
                for(Object item : componentsCheckComboBox.getCheckModel().getCheckedItems()) {
                    query.append(" and SpellComponents like ?");
                    vars.add('%' + String.valueOf(item) + '%');
                }
            }
            try {
                tableSpells.clear();
                PreparedStatement prepdStmt = conn.prepareStatement(query.toString());
                for (int i = 0; i < vars.size(); i++) prepdStmt.setString(i + 1, String.valueOf(vars.get(i)));
                ResultSet set = prepdStmt.executeQuery();
                while(set.next()) tableSpells.add(new Spell(set.getString(1)));
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            mw_SpellTable.setItems(tableSpells);
            updateSpellsFound();
            return true;
        } else {
            // None of the focus fields were filled in, query the entire database.
            tableSpells.clear();
            try(Statement statement = conn.createStatement(); ResultSet set = statement.executeQuery(MassNameLookup)) {
                while(set.next()) tableSpells.add(new Spell(set.getString(1)));
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            mw_SpellTable.setItems(tableSpells);
            updateSpellsFound();
            return true;
        }
    }

    private void buildComboBoxClasses() {
        ObservableList<String> classes = FXCollections.observableArrayList(
                "Bard", "Cleric", "Druid", "Paladin", "Ranger", "Sorcerer", "Warlock", "Wizard"
        );
        this.classCheckComboBox = new CheckComboBox<>(classes);
        classCheckComboBox.setMinWidth(MINMAX_WIDTH);
        classCheckComboBox.setMaxWidth(MINMAX_WIDTH);
        mw_NarrowGrid.add(classCheckComboBox, 1, 2);
    }

    private void buildComboBoxComponents() {
        ObservableList<String> descriptors = FXCollections.observableArrayList(
                "Verbal (V)", "Somatic (S)", "Material (M)", "Focus (F)", "Divine Focus (DF)", "XP Cost (XP)"
        );
        this.componentsCheckComboBox = new CheckComboBox<>(descriptors);
        componentsCheckComboBox.setMinWidth(MINMAX_WIDTH);
        componentsCheckComboBox.setMaxWidth(MINMAX_WIDTH);
        mw_NarrowGrid.add(componentsCheckComboBox, 1, 3);
    }

    /**
     * Called when the user releases the level slider.
     *
     * On Action: Update the label @SpellSearcher_LvlLabel based on the slider's current value.
     */
    @FXML
    public void levelSliderChanged() {
        switch ((int) mw_SpellLevel_Slider.getValue()) {
            case 0:
                mw_SpellLevel_Lbl.setText("[Cantrips]");
                break;
            case 10:
                mw_SpellLevel_Lbl.setText("[All Levels]");
                break;
            default:
                mw_SpellLevel_Lbl.setText("[" + (int) mw_SpellLevel_Slider.getValue() + "]");
                break;
        }
    }

    private void buildSummary(String selection) {
        Connection conn = MagicWand.openConnection();
        if (conn != null) {
            try {
                PreparedStatement prepdStmt = conn.prepareStatement(LikeDescLookup);
                prepdStmt.setString(1, selection);
                ResultSet set = prepdStmt.executeQuery();
                if(set.next()) {
                    // Build summary
                    mw_SumPan_1.setText(set.getString(1));
                    mw_SumPan_2.setText(set.getString(3));
                    mw_SumPan_3.setText(set.getString(4));
                    mw_SumPan_4.setText(set.getString(5));
                    mw_SumPan_5.setText(set.getString(6));
                    mw_SumPan_6.setText(set.getString(7));
                    mw_SumPan_7.setText(set.getString(8));
                    mw_SumPan_8.setText(set.getString(9));
                    // Now, set the description.
                    String desc = set.getString(2);
                    // Perform some text cleanup before setting the description...
                    if(desc.startsWith("\n")) desc = desc.replaceFirst("\n", "");
                    mw_SpellDescTxtArea.setText(desc);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            mw_SpellTable.setItems(tableSpells);
        } else {
            System.out.println("Unable to open database connection.");
        }
    }

    /**
     * Called when the user clicked or selects an item in the table.
     *
     * On Action: Update the mw_SpellDescTxtArea to match the description of the selected spell. Also, update the mw_Sum_Showing label to indicate the selected spell.
     */
    @FXML
    public void onSpellTableClicked() {
        mw_SumPan_0.setText(mw_SpellTable.getSelectionModel().getSelectedItem().getSpellName());
        buildSummary(mw_SumPan_0.getText());
    }

    /**
     * Sets a specific label how many spells are currently displaying in the table based on the filter criteria.
     */
    public void updateSpellsFound() {
        mw_SpellsFound.setText(String.valueOf(mw_SpellTable.getItems().size()) + " spells found.");
    }

    /**
     * Resets the filter criteria.
     */
    @FXML
    public void onClearAllFields() {
        mw_SpellName.setText("");
        mw_SpellSchool.getSelectionModel().select(0);
        mw_SpellLevel_Slider.setValue(10);
    }

}
