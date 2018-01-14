import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        DBConnection sqlCon = new DBConnection();
        sqlCon.getPLintReport();

    }
}

class DBConnection {
    private Connection sqlConnection;

    public DBConnection() {
        try {
            connectToDB();
        } catch (ClassNotFoundException e) {
            System.out.println("[Error connecting to DB]: " + e);
        }
    }

    //Establishes connection to database that exists in same directory
    public void connectToDB() throws ClassNotFoundException{
        Class.forName("org.sqlite.JDBC");

        try{
            sqlConnection = DriverManager.getConnection("jdbc:sqlite:../P-Lint/results");
            sqlConnection.setAutoCommit(false);
        }
        catch(SQLException se){
            System.out.println("[Error connecting to DB]: " + se);
        }
    }

    public void addReport(String appName,String commitGuid,String author,String fileName,int usecaseId,String state){
        try{
                Statement stmt = sqlConnection.createStatement();
                String sql = "INSERT INTO commit_status_report (app_name,commit_guid,author,file_name,usecase,state) " +
                        "VALUES (\""+appName+"\",\""+commitGuid+"\",\"" + author + "\",\"" + fileName + "\"," +
                        usecaseId + ",\"" + state + "\")";

                stmt.executeUpdate(sql);

                stmt.close();
                sqlConnection.commit();

        }
        catch(SQLException se){
            System.out.println("[Error Adding data to db]: "+se);
        }
    }

    public void getPLintReport() {
        Map<String,String> data = new HashMap<>();

        try {
            Statement stmt = sqlConnection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT \n" +
                            "apps.app_name ,\n" +
                            "apks.apk_name commit_guid,\n" +
                            "apks.apk_author_name author,\n" +
                            "reports.file_name,\n" +
                            "case Count( case when reports.usecase_id=1 then reports.usecase_id end) when 0 then '0' else 'X' end AS 'uc1',\n" +
                            "case Count( case when reports.usecase_id=2 then reports.usecase_id end) when 0 then '0' else 'X' end AS 'uc2',\n" +
                            "case Count( case when reports.usecase_id=3 then reports.usecase_id end) when 0 then '0' else 'X' end AS 'uc3',\n" +
                            "case Count( case when reports.usecase_id=4 then reports.usecase_id end) when 0 then '0' else 'X' end AS 'uc4',\n" +
                            "case Count( case when reports.usecase_id=5 then reports.usecase_id end) when 0 then '0' else 'X' end AS 'uc5',\n" +
                            "case Count( case when reports.usecase_id=6 then reports.usecase_id end) when 0 then '0' else 'X' end AS 'uc6',\n" +
                            "case Count( case when reports.usecase_id=7 then reports.usecase_id end) when 0 then '0' else 'X' end AS 'uc7',\n" +
                            "case Count( case when reports.usecase_id=8 then reports.usecase_id end) when 0 then '0' else 'X' end AS 'uc8',\n" +
                            "case Count( case when reports.usecase_id=9 then reports.usecase_id end) when 0 then '0' else 'X' end AS 'uc9',\n" +
                            "case Count( case when reports.usecase_id=10 then reports.usecase_id end) when 0 then '0' else 'X' end AS 'uc10',\n" +
                            "case Count( case when reports.usecase_id=11 then reports.usecase_id end) when 0 then '0' else 'X' end AS 'uc11',\n" +
                            "case Count( case when reports.usecase_id=12 then reports.usecase_id end) when 0 then '0' else 'X' end AS 'uc12',\n" +
                            "case Count( case when reports.usecase_id=13 then reports.usecase_id end) when 0 then '0' else 'X' end AS 'uc13',\n" +
                            "case Count( case when reports.usecase_id=14 then reports.usecase_id end) when 0 then '0' else 'X' end AS 'uc14',\n" +
                            "case Count( case when reports.usecase_id=15 then reports.usecase_id end) when 0 then '0' else 'X' end AS 'uc15',\n" +
                            "case Count( case when reports.usecase_id=16 then reports.usecase_id end) when 0 then '0' else 'X' end AS 'uc16',\n" +
                            "case Count( case when reports.usecase_id=17 then reports.usecase_id end) when 0 then '0' else 'X' end AS 'uc17'\n" +
                            "FROM apps  \n" +
                            "LEFT OUTER JOIN apks \n" +
                            "ON (apps.dataset_app_id = apks.app_id) \n" +
                            "LEFT OUTER JOIN reports\n" +
                            "ON (apks.apk_id=reports.apk_id)\n" +
                            "LEFT OUTER JOIN use_cases\n" +
                            "ON (reports.usecase_id=use_cases.usecase_id)\n" +
                            "GROUP BY \n" +
                            "apps.app_name,reports.file_name, apks.apk_name,apks.apk_author_name\n" +
                            "ORDER BY apks.app_id, reports.file_name;" );


            String[] fields = new String[20];

            while(rs.next()) {
                //System.err.println(rs.getString("uc1"));
                //if fields empty then first time in loop
                if (fields[0]!=null && fields[0]!=""){

                    //if app name is the same as last iteration
                    if (fields[0].toString().equals((rs.getString("app_name")))){
                        //check if we are working with the same file
                        if (rs.getString("file_name")!=null) {

                            if (fields[19].toString().equals(rs.getString("file_name"))) {

                                //We have to check each usecase and see the previous result.
                                String currentState = "-";
                                if (fields[1].toString().equals("X")&& rs.getString("uc1").equals("X"))
                                    currentState = "-";
                                if (fields[1].toString().equals("X")&& rs.getString("uc1").equals("0"))
                                    currentState = "FIXED";
                                if (fields[1].toString().equals("0")&& rs.getString("uc1").equals("X"))
                                    currentState = "BREAK";

                                //Insert usecase
                                addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                        rs.getString("author"), rs.getString("file_name"), 1,
                                        currentState);

                                currentState = "-";
                                if (fields[2].toString().equals("X")&& rs.getString("uc2").equals("X"))
                                    currentState = "-";
                                if (fields[2].toString().equals("X")&& rs.getString("uc2").equals("0"))
                                    currentState = "FIXED";
                                if (fields[2].toString().equals("0")&& rs.getString("uc2").equals("X"))
                                    currentState = "BREAK";

                                //Insert usecase
                                addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                        rs.getString("author"), rs.getString("file_name"), 2,
                                        currentState);

                                currentState = "-";
                                if (fields[3].toString().equals("X")&& rs.getString("uc3").equals("X"))
                                    currentState = "-";
                                if (fields[3].toString().equals("X")&& rs.getString("uc3").equals("0"))
                                    currentState = "FIXED";
                                if (fields[3].toString().equals("0")&& rs.getString("uc3").equals("X"))
                                    currentState = "BREAK";

                                //Insert usecase
                                addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                        rs.getString("author"), rs.getString("file_name"), 3,
                                        currentState);

                                currentState = "-";
                                if (fields[4].toString().equals("X")&& rs.getString("uc4").equals("X"))
                                    currentState = "-";
                                if (fields[4].toString().equals("X")&& rs.getString("uc4").equals("0"))
                                    currentState = "FIXED";
                                if (fields[4].toString().equals("0")&& rs.getString("uc4").equals("X"))
                                    currentState = "BREAK";

                                //Insert usecase
                                addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                        rs.getString("author"), rs.getString("file_name"), 4,
                                        currentState);

                                currentState = "-";
                                if (fields[5].toString().equals("X")&& rs.getString("uc5").equals("X"))
                                    currentState = "-";
                                if (fields[5].toString().equals("X")&& rs.getString("uc5").equals("0"))
                                    currentState = "FIXED";
                                if (fields[5].toString().equals("0")&& rs.getString("uc5").equals("X"))
                                    currentState = "BREAK";

                                //Insert usecase
                                addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                        rs.getString("author"), rs.getString("file_name"), 5,
                                        currentState);

                                currentState = "-";
                                if (fields[6].toString().equals("X")&& rs.getString("uc6").equals("X"))
                                    currentState = "-";
                                if (fields[6].toString().equals("X")&& rs.getString("uc6").equals("0"))
                                    currentState = "FIXED";
                                if (fields[6].toString().equals("0")&& rs.getString("uc6").equals("X"))
                                    currentState = "BREAK";

                                //Insert usecase
                                addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                        rs.getString("author"), rs.getString("file_name"), 6,
                                        currentState);

                                currentState = "-";
                                if (fields[7].toString().equals("X")&& rs.getString("uc7").equals("X"))
                                    currentState = "-";
                                if (fields[7].toString().equals("X")&& rs.getString("uc7").equals("0"))
                                    currentState = "FIXED";
                                if (fields[7].toString().equals("0")&& rs.getString("uc7").equals("X"))
                                    currentState = "BREAK";

                                //Insert usecase
                                addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                        rs.getString("author"), rs.getString("file_name"), 2,
                                        currentState);

                                currentState = "-";
                                if (fields[8].toString().equals("X")&& rs.getString("uc8").equals("X"))
                                    currentState = "-";
                                if (fields[8].toString().equals("X")&& rs.getString("uc8").equals("0"))
                                    currentState = "FIXED";
                                if (fields[8].toString().equals("0")&& rs.getString("uc8").equals("X"))
                                    currentState = "BREAK";

                                //Insert usecase
                                addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                        rs.getString("author"), rs.getString("file_name"), 8,
                                        currentState);

                                currentState = "-";
                                if (fields[9].toString().equals("X")&& rs.getString("uc9").equals("X"))
                                    currentState = "-";
                                if (fields[9].toString().equals("X")&& rs.getString("uc9").equals("0"))
                                    currentState = "FIXED";
                                if (fields[9].toString().equals("0")&& rs.getString("uc9").equals("X"))
                                    currentState = "BREAK";

                                //Insert usecase
                                addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                        rs.getString("author"), rs.getString("file_name"), 9,
                                        currentState);

                                currentState = "-";
                                if (fields[10].toString().equals("X")&& rs.getString("uc10").equals("X"))
                                    currentState = "-";
                                if (fields[10].toString().equals("X")&& rs.getString("uc10").equals("0"))
                                    currentState = "FIXED";
                                if (fields[10].toString().equals("0")&& rs.getString("uc10").equals("X"))
                                    currentState = "BREAK";

                                //Insert usecase
                                addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                        rs.getString("author"), rs.getString("file_name"), 10,
                                        currentState);

                                currentState = "-";
                                if (fields[11].toString().equals("X")&& rs.getString("uc11").equals("X"))
                                    currentState = "-";
                                if (fields[11].toString().equals("X")&& rs.getString("uc11").equals("0"))
                                    currentState = "FIXED";
                                if (fields[11].toString().equals("0")&& rs.getString("uc11").equals("X"))
                                    currentState = "BREAK";

                                //Insert usecase
                                addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                        rs.getString("author"), rs.getString("file_name"), 11,
                                        currentState);

                                currentState = "-";
                                if (fields[12].toString().equals("X")&& rs.getString("uc12").equals("X"))
                                    currentState = "-";
                                if (fields[12].toString().equals("X")&& rs.getString("uc12").equals("0"))
                                    currentState = "FIXED";
                                if (fields[12].toString().equals("0")&& rs.getString("uc12").equals("X"))
                                    currentState = "BREAK";

                                //Insert usecase
                                addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                        rs.getString("author"), rs.getString("file_name"), 12,
                                        currentState);

                                currentState = "-";
                                if (fields[13].toString().equals("X")&& rs.getString("uc13").equals("X"))
                                    currentState = "-";
                                if (fields[13].toString().equals("X")&& rs.getString("uc13").equals("0"))
                                    currentState = "FIXED";
                                if (fields[13].toString().equals("0")&& rs.getString("uc13").equals("X"))
                                    currentState = "BREAK";

                                //Insert usecase
                                addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                        rs.getString("author"), rs.getString("file_name"), 13,
                                        currentState);

                                currentState = "-";
                                if (fields[14].toString().equals("X")&& rs.getString("uc14").equals("X"))
                                    currentState = "-";
                                if (fields[14].toString().equals("X")&& rs.getString("uc14").equals("0"))
                                    currentState = "FIXED";
                                if (fields[14].toString().equals("0")&& rs.getString("uc14").equals("X"))
                                    currentState = "BREAK";

                                //Insert usecase
                                addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                        rs.getString("author"), rs.getString("file_name"), 14,
                                        currentState);

                                currentState = "-";
                                if (fields[15].toString().equals("X")&& rs.getString("uc15").equals("X"))
                                    currentState = "-";
                                if (fields[15].toString().equals("X")&& rs.getString("uc15").equals("0"))
                                    currentState = "FIXED";
                                if (fields[15].toString().equals("0")&& rs.getString("uc15").equals("X"))
                                    currentState = "BREAK";

                                //Insert usecase
                                addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                        rs.getString("author"), rs.getString("file_name"), 15,
                                        currentState);

                                currentState = "-";
                                if (fields[16].toString().equals("X")&& rs.getString("uc16").equals("X"))
                                    currentState = "-";
                                if (fields[16].toString().equals("X")&& rs.getString("uc16").equals("0"))
                                    currentState = "FIXED";
                                if (fields[16].toString().equals("0")&& rs.getString("uc16").equals("X"))
                                    currentState = "BREAK";

                                //Insert usecase
                                addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                        rs.getString("author"), rs.getString("file_name"), 16,
                                        currentState);

                                currentState = "-";
                                if (fields[17].toString().equals("X")&& rs.getString("uc17").equals("X"))
                                    currentState = "-";
                                if (fields[17].toString().equals("X")&& rs.getString("uc17").equals("0"))
                                    currentState = "FIXED";
                                if (fields[17].toString().equals("0")&& rs.getString("uc17").equals("X"))
                                    currentState = "BREAK";

                                //Insert usecase
                                addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                        rs.getString("author"), rs.getString("file_name"), 17,
                                        currentState);


                            } else {
                                //first time with new file
                                addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                        rs.getString("author"),rs.getString("file_name"),1,
                                        rs.getString("uc1").equals("X")?"BREAK":"-");

                                addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                        rs.getString("author"),rs.getString("file_name"),2,
                                        rs.getString("uc2").equals("X")?"BREAK":"-");

                                addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                        rs.getString("author"),rs.getString("file_name"),3,
                                        rs.getString("uc3").equals("X")?"BREAK":"-");

                                addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                        rs.getString("author"),rs.getString("file_name"),4,
                                        rs.getString("uc4").equals("X")?"BREAK":"-");

                                addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                        rs.getString("author"),rs.getString("file_name"),5,
                                        rs.getString("uc5").equals("X")?"BREAK":"-");

                                addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                        rs.getString("author"),rs.getString("file_name"),6,
                                        rs.getString("uc6").equals("X")?"BREAK":"-");

                                addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                        rs.getString("author"),rs.getString("file_name"),7,
                                        rs.getString("uc7").equals("X")?"BREAK":"-");

                                addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                        rs.getString("author"),rs.getString("file_name"),8,
                                        rs.getString("uc8").equals("X")?"BREAK":"-");

                                addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                        rs.getString("author"),rs.getString("file_name"),9,
                                        rs.getString("uc9").equals("X")?"BREAK":"-");

                                addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                        rs.getString("author"),rs.getString("file_name"),10,
                                        rs.getString("uc10").equals("X")?"BREAK":"-");

                                addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                        rs.getString("author"),rs.getString("file_name"),11,
                                        rs.getString("uc11").equals("X")?"BREAK":"-");

                                addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                        rs.getString("author"),rs.getString("file_name"),12,
                                        rs.getString("uc12").equals("X")?"BREAK":"-");

                                addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                        rs.getString("author"),rs.getString("file_name"),13,
                                        rs.getString("uc13").equals("X")?"BREAK":"-");

                                addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                        rs.getString("author"),rs.getString("file_name"),14,
                                        rs.getString("uc14").equals("X")?"BREAK":"-");

                                addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                        rs.getString("author"),rs.getString("file_name"),15,
                                        rs.getString("uc15").equals("X")?"BREAK":"-");

                                addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                        rs.getString("author"),rs.getString("file_name"),16,
                                        rs.getString("uc16").equals("X")?"BREAK":"-");

                                addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                        rs.getString("author"),rs.getString("file_name"),17,
                                        rs.getString("uc17").equals("X")?"BREAK":"-");

                            }
                        }else {
                        //if filename is null  means manifest wasnt found in project
                            addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                    rs.getString("author"), rs.getString("file_name"), 1,
                                    "-");
                            addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                    rs.getString("author"), rs.getString("file_name"), 2,
                                    "-");
                            addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                    rs.getString("author"), rs.getString("file_name"), 3,
                                    "-");
                            addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                    rs.getString("author"), rs.getString("file_name"), 4,
                                    "-");
                            addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                    rs.getString("author"), rs.getString("file_name"), 5,
                                    "-");
                            addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                    rs.getString("author"), rs.getString("file_name"), 6,
                                    "-");
                            addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                    rs.getString("author"), rs.getString("file_name"), 7,
                                    "-");
                            addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                    rs.getString("author"), rs.getString("file_name"), 8,
                                    "-");
                            addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                    rs.getString("author"), rs.getString("file_name"), 9,
                                    "-");
                            addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                    rs.getString("author"), rs.getString("file_name"), 10,
                                    "-");
                            addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                    rs.getString("author"), rs.getString("file_name"), 11,
                                    "-");
                            addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                    rs.getString("author"), rs.getString("file_name"), 12,
                                    "-");
                            addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                    rs.getString("author"), rs.getString("file_name"), 13,
                                    "-");
                            addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                    rs.getString("author"), rs.getString("file_name"), 14,
                                    "-");
                            addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                    rs.getString("author"), rs.getString("file_name"), 15,
                                    "-");
                            addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                    rs.getString("author"), rs.getString("file_name"), 16,
                                    "-");
                            addReport(rs.getString("app_name"), rs.getString("commit_guid"),
                                    rs.getString("author"), rs.getString("file_name"), 17,
                                    "-");

                        }

                    }else {
                        addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                rs.getString("author"),rs.getString("file_name"),1,
                                rs.getString("uc1").equals("X")?"BREAK":"-");

                        addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                rs.getString("author"),rs.getString("file_name"),2,
                                rs.getString("uc2").equals("X")?"BREAK":"-");

                        addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                rs.getString("author"),rs.getString("file_name"),3,
                                rs.getString("uc3").equals("X")?"BREAK":"-");

                        addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                rs.getString("author"),rs.getString("file_name"),4,
                                rs.getString("uc4").equals("X")?"BREAK":"-");

                        addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                rs.getString("author"),rs.getString("file_name"),5,
                                rs.getString("uc5").equals("X")?"BREAK":"-");

                        addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                rs.getString("author"),rs.getString("file_name"),6,
                                rs.getString("uc6").equals("X")?"BREAK":"-");

                        addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                rs.getString("author"),rs.getString("file_name"),7,
                                rs.getString("uc7").equals("X")?"BREAK":"-");

                        addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                rs.getString("author"),rs.getString("file_name"),8,
                                rs.getString("uc8").equals("X")?"BREAK":"-");

                        addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                rs.getString("author"),rs.getString("file_name"),9,
                                rs.getString("uc9").equals("X")?"BREAK":"-");

                        addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                rs.getString("author"),rs.getString("file_name"),10,
                                rs.getString("uc10").equals("X")?"BREAK":"-");

                        addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                rs.getString("author"),rs.getString("file_name"),11,
                                rs.getString("uc11").equals("X")?"BREAK":"-");

                        addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                rs.getString("author"),rs.getString("file_name"),12,
                                rs.getString("uc12").equals("X")?"BREAK":"-");

                        addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                rs.getString("author"),rs.getString("file_name"),13,
                                rs.getString("uc13").equals("X")?"BREAK":"-");

                        addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                rs.getString("author"),rs.getString("file_name"),14,
                                rs.getString("uc14").equals("X")?"BREAK":"-");

                        addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                rs.getString("author"),rs.getString("file_name"),15,
                                rs.getString("uc15").equals("X")?"BREAK":"-");

                        addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                rs.getString("author"),rs.getString("file_name"),16,
                                rs.getString("uc16").equals("X")?"BREAK":"-");

                        addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                                rs.getString("author"),rs.getString("file_name"),17,
                                rs.getString("uc17").equals("X")?"BREAK":"-");

                    }

                }else
                {
                    addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                            rs.getString("author"),rs.getString("file_name"),1,
                            rs.getString("uc1").equals("X")?"BREAK":"-");

                    addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                            rs.getString("author"),rs.getString("file_name"),2,
                            rs.getString("uc2").equals("X")?"BREAK":"-");

                    addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                            rs.getString("author"),rs.getString("file_name"),3,
                            rs.getString("uc3").equals("X")?"BREAK":"-");

                    addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                            rs.getString("author"),rs.getString("file_name"),4,
                            rs.getString("uc4").equals("X")?"BREAK":"-");

                    addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                            rs.getString("author"),rs.getString("file_name"),5,
                            rs.getString("uc5").equals("X")?"BREAK":"-");

                    addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                            rs.getString("author"),rs.getString("file_name"),6,
                            rs.getString("uc6").equals("X")?"BREAK":"-");

                    addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                            rs.getString("author"),rs.getString("file_name"),7,
                            rs.getString("uc7").equals("X")?"BREAK":"-");

                    addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                            rs.getString("author"),rs.getString("file_name"),8,
                            rs.getString("uc8").equals("X")?"BREAK":"-");

                    addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                            rs.getString("author"),rs.getString("file_name"),9,
                            rs.getString("uc9").equals("X")?"BREAK":"-");

                    addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                            rs.getString("author"),rs.getString("file_name"),10,
                            rs.getString("uc10").equals("X")?"BREAK":"-");

                    addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                            rs.getString("author"),rs.getString("file_name"),11,
                            rs.getString("uc11").equals("X")?"BREAK":"-");

                    addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                            rs.getString("author"),rs.getString("file_name"),12,
                            rs.getString("uc12").equals("X")?"BREAK":"-");

                    addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                            rs.getString("author"),rs.getString("file_name"),13,
                            rs.getString("uc13").equals("X")?"BREAK":"-");

                    addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                            rs.getString("author"),rs.getString("file_name"),14,
                            rs.getString("uc14").equals("X")?"BREAK":"-");

                    addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                            rs.getString("author"),rs.getString("file_name"),15,
                            rs.getString("uc15").equals("X")?"BREAK":"-");

                    addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                            rs.getString("author"),rs.getString("file_name"),16,
                            rs.getString("uc16").equals("X")?"BREAK":"-");

                    addReport(rs.getString("app_name"),rs.getString("commit_guid"),
                            rs.getString("author"),rs.getString("file_name"),17,
                            rs.getString("uc17").equals("X")?"BREAK":"-");



                }

                fields[0]=rs.getString("app_name");
                fields[1]=rs.getString("uc1");
                fields[2]=rs.getString("uc2");
                fields[3]=rs.getString("uc3");
                fields[4]=rs.getString("uc4");
                fields[5]=rs.getString("uc5");
                fields[6]=rs.getString("uc6");
                fields[7]=rs.getString("uc7");
                fields[8]=rs.getString("uc8");
                fields[9]=rs.getString("uc9");
                fields[10]=rs.getString("uc10");
                fields[11]=rs.getString("uc11");
                fields[12]=rs.getString("uc12");
                fields[13]=rs.getString("uc13");
                fields[14]=rs.getString("uc14");
                fields[15]=rs.getString("uc15");
                fields[16]=rs.getString("uc16");
                fields[17]=rs.getString("uc17");
                fields[18]=rs.getString("commit_guid");
                fields[19]=rs.getString("file_name");

            }

            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println("[Error getting getDatasetList]: " + e.getMessage() );
        }

    }
}