package com.konus.pereklichka;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.konus.pereklichka.rv_models.GroupModel;
import com.konus.pereklichka.rv_models.MemberModel;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import org.apache.poi.ss.usermodel.Row;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;


public class SQLiteManager extends SQLiteOpenHelper {
    private static SQLiteManager sqLiteManager;
    private static final String DATABASE_NAME = "UsersDB";
    private static final int DATABASE_VERSION = 1;
    public String TABLE_NAME;
    private static final String COUNTER = "counter";
    private static final String ID_FIELD = "id";
    private static final String NAME_FIELD = "name";
    private static final String LASTNAME_FIELD = "lastname";
    public static SQLiteManager instanceOfDatabase(Context context, String tb_name) {
        if (sqLiteManager == null || !sqLiteManager.TABLE_NAME.equals(tb_name)) {
            sqLiteManager = new SQLiteManager(context, tb_name);
        }
        return sqLiteManager;
    }

    private SQLiteManager(Context context, String tb_name) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.TABLE_NAME = tb_name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sql;
        sql = new StringBuilder()
                .append("CREATE TABLE IF NOT EXISTS ")
                .append("groups_table")
                .append("(")
                .append(COUNTER)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append("group_name")
                .append(" TEXT, ")
                .append("members_amount")
                .append(" INT)");
        db.execSQL(sql.toString());

    }


    public void addNewTable() {
        SQLiteDatabase db = getWritableDatabase();
        StringBuilder sql;
        sql = new StringBuilder()
                .append("CREATE TABLE IF NOT EXISTS ")
                .append(TABLE_NAME)
                .append("(")
                .append(COUNTER)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(ID_FIELD)
                .append(" INT,")
                .append(NAME_FIELD)
                .append(" TEXT, ")
                .append(LASTNAME_FIELD)
                .append(" TEXT)");
        db.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void deleteTable() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM groups_table WHERE group_name =? ", new String[]{TABLE_NAME});
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }


    public void addUserToDB(MemberModel memberModel){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_FIELD, memberModel.getSpecial_id());
        contentValues.put(NAME_FIELD, memberModel.getTxtName());
        contentValues.put(LASTNAME_FIELD, memberModel.getTxtLName());
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public ArrayList<MemberModel> loadMembersFromDB(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<MemberModel> memberModels= new ArrayList<>();
        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null)) {
            if (result.getCount()!=0){
                while (result.moveToNext()){
                    String id = result.getString(1);
                    String name = result.getString(2);
                    String last_name = result.getString(3);

                    memberModels.add(new MemberModel(name,last_name,false,id));

                }
                return memberModels;
            }
        }
        return null;
    }

    public ArrayList<GroupModel> loadGroupsFromDB(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<GroupModel> groups= new ArrayList<>();
        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + "groups_table", null)) {
            if (result.getCount()!=0){
                while (result.moveToNext()){
                    String name = result.getString(1);
                    String amount = result.getString(2);
                    GroupModel model = new GroupModel(amount,name);
                    groups.add(model);

                }
                return groups;
            }
        }
        return null;
    }


    public void addGroupToDB(GroupModel groupModel){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("group_name", groupModel.getgroup_name_txt());
        contentValues.put("members_amount", groupModel.getmemberAmountTxt());
        sqLiteDatabase.insert("groups_table", null, contentValues);
    }


    public void updateUserInDB(MemberModel memberModel){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_FIELD, memberModel.getSpecial_id());
        contentValues.put(NAME_FIELD, memberModel.getTxtName());
        contentValues.put(LASTNAME_FIELD, memberModel.getTxtLName());
        sqLiteDatabase.update(TABLE_NAME, contentValues, ID_FIELD + " =? ", new String[]{memberModel.getSpecial_id()});
    }

    public void updateUserAmountInDB(String groupname) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        int amount = 0;
        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM groups_table WHERE group_name=?", new String[]{groupname})) {
            if (result.getCount() > 0 && result.moveToFirst()) {
                int amountColumnIndex = result.getColumnIndex("members_amount");
                if (amountColumnIndex != -1) {
                    amount = result.getInt(amountColumnIndex);
                }
            }
        } catch (Exception e) {
            System.err.println("HERE" + e);
        }

        contentValues.put("group_name", groupname);
        contentValues.put("members_amount", amount + 1);
        sqLiteDatabase.update("groups_table", contentValues, "group_name=?", new String[]{groupname});


    }

    public void exportToExcel(Context context) {
        ArrayList<MemberModel> MemberModels = loadMembersFromDB();
        XSSFWorkbook workbook = new XSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Sheet1");


        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Ім'я");
        headerRow.createCell(2).setCellValue("Прізвище");
        headerRow.createCell(3).setCellValue("Присутність");
        headerRow.createCell(4).setCellValue("");
        headerRow.createCell(5).setCellValue("Дата:");
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = sdf.format(date);
        String formattedTime = sdf2.format(date);
        headerRow.createCell(6).setCellValue(formattedDate);
        headerRow.createCell(7).setCellValue("Час:");
        headerRow.createCell(8).setCellValue(formattedTime);


        int rowNum = 1;
        for (MemberModel data : MemberModels) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.getSpecial_id());
            row.createCell(1).setCellValue(data.getTxtName());
            row.createCell(2).setCellValue(data.getTxtLName());
            if (data.getImage()){
            row.createCell(3).setCellValue("Присутній");}
            else {
                row.createCell(3).setCellValue("Відсутній");
            }

        }


        File file = saveWorkbookToTempFile(context, workbook);
        assert file != null;
        Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(Intent.createChooser(intent, "Export to Excel"));
    }

    private File saveWorkbookToTempFile(Context context, XSSFWorkbook workbook) {
        try {
            // Create a temporary file in external storage
            File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "ExcelExports");
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("Could not create directories");
            }
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
            String formattedDate = sdf.format(date);
            File file = new File(dir, TABLE_NAME + "_"+formattedDate+".xlsx");

            if (!file.exists() && !file.createNewFile()) {
                throw new IOException("Could not create file");
            }

            // Write the workbook content to the file
            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();

            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
