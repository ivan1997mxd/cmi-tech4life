package com.estethapp.media.mSparrow;

/******************************************************************************
 *
 * Sheridan’s Centre for Mobile Innovation (CMI)
 *
 * All Rights Reserved.
 * © Copyright by The Sheridan College Institute of Technology and Advanced
 *   Learning’s (“Sheridan”) Centre for Mobile Innovation, September 2019
 *
 *
 * NOTICE:
 * All information contained herein is, and remains the property of Sheridan.
 * The intellectual and technical concepts contained herein are proprietary
 * to Sheridan and its suppliers, affiliates, and subsidiaries, and may be
 * covered by Canadian, U.S. and Foreign Patents, patents in process, and
 * are protected by trade secret or copyright law.
 * Dissemination of this information, reproduction of this material, or use
 * of this information for any purpose other than permission which is
 * expressly given by Sheridan is strictly forbidden unless prior explicit
 * written permission is obtained from Sheridan (and/or its
 * Centre for Mobile Innovation).
 *
 * Grant Funding that enabled the development of all information contained
 * herein:
 *   Natural Sciences and Engineering Research Council of Canada (NSERC)
 *   Grant #: Sheridan Account Department #06227
 *            NSERC CCIP - IE 503351-16 CMI
 *   Description:
 *   http://www.nserc-crsng.gc.ca/Professors-Professeurs/RPP-PP/CCI-ICC_eng.asp
 *     Created:  Warren Zajac on 7/1/2020
 *     E: WarrenZajac@gmail.com
 * ****************************************************************************
 */

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class APIService {

    // Static Globals
    // Base Server URI
    // Sheridan Private Server IP Address
    public static final String CMI_SERVER_URL = "http://142.55.32.86:50161/record/dataUpload.php";
    // Tech4Life Production Server URI
    private static final String SERVER_URI = "https://mdconsults.org/MDCAPI/public/createCaseVital";
    // Private testing URL WarrenZajac Posting server
    private static final String XMCS_SERVER_URI = "http://xmcs.org/ddy.php";

    // Static Case Code Value
    private static final String CASE_CODE = "871";

    // Value of all unset items are defaulted to this String
    private static final String defaultUnSet = "NA";

    // Default Debug State
    boolean mDebugEnabled = true;

    // Returned status json message
    private String retString;

    // API Variables unknown but required

    // Patient Weight
    private String Weight;
    private String WeightUnit;

    // Patient Height
    private String Height;
    private String HeightUnit;

    private String PatientPainPresent;  // Pain is Present (Yes/No)
    private String PainPatient;         // Pain Level 0-10
    private String CityID;                // City ID for data location

    private String Data;
    private String Diagnosis;
    private String DiseaseID;
    private String Gender;
    private String History;
    private String LocationID;
    private String PatientID;
    private String UsersID;
    private String Comment;
    private String m_age;
    private String y_age;
    private String created_at;

    private static final boolean EXTENDED_DEBUG = false;

    // Debugging Tag Name Selection for vLog
    private final String TAG = this.getClass().getSimpleName();

    // get/set (.....) defaults
    private String CaseCode;
    private String FormID;
    private String RefererID;
    private String PatientFirstName;
    private String PatientLastName;
    private String Temperature;
    private String TemperatureValue;
    private String OralTemperature;
    private String Systolic;
    private String Diastolic;
    private String Hands;
    private String BloodPressurePosition;
    private String PulsePres;
    private String HeartPulseRate;
    private String RespRate;
    private String Saturation;
    private String OxygenSaturation;

    // Enable URL string padding of the post command
    //  Not usually required
    private boolean enableURLPadding;

    public APIService(String patientID, String refererID, String UsersID) {

        defaultInit();

        this.RefererID = refererID;
        this.PatientID = patientID;
        this.UsersID = UsersID;

    }
        // Initialise Data for API Posting
    public APIService() {

        defaultInit();

    }

    private void defaultInit() {
        // Default for URL Padding Feature (POST URL Request)
        enableURLPadding = false;

        // Set Default Values for API
        TemperatureValue = "℃";
        Temperature = defaultUnSet;
        //"NA";

        PatientLastName = defaultUnSet;
        //"Doe";
        PatientFirstName = defaultUnSet;

        OralTemperature = "Oral";

        Systolic = defaultUnSet;
        Diastolic = defaultUnSet;
        Hands = defaultUnSet;
        BloodPressurePosition = defaultUnSet;

        PulsePres = defaultUnSet;
        HeartPulseRate = defaultUnSet;
        RespRate = defaultUnSet;

        Saturation = defaultUnSet;
        OxygenSaturation = defaultUnSet;

        // Set IDs for Sample Record
        RefererID       = "829";
        FormID          = "1";
        DiseaseID       = "1";
        LocationID      = "35";
        CityID          = "1";
        PatientID       = "0";
        UsersID         = "829";

        CaseCode = CASE_CODE;

        // Weight
        Weight = defaultUnSet;
        WeightUnit = "kg";

        // Height
        Height = defaultUnSet;
        HeightUnit = "inches";

        // PainPresent? Set to no
        setPainPatient("0");

        Data = defaultUnSet;
        Diagnosis = defaultUnSet;
        Gender = "f";
        History = defaultUnSet;
        m_age = "3";    // Month
        y_age = "20";  // Year
        Comment = defaultUnSet;
        created_at = "2020-07-21 1:30:37";

        // Clear return status string
        retString = "";

    }

    // Populate field data with sample demo information
    //  Used for validation and testing of posting data to server
    public void setPopulateDemoData()
    {
        TemperatureValue = "℃";
        Temperature = "34";

        PatientLastName = "Doe";
        PatientFirstName = "John";

        OralTemperature = "Oral";
        Systolic = "125";
        Diastolic = "90";
        Hands = "Left";
        BloodPressurePosition = "Lying";

        PulsePres = "Regular";
        HeartPulseRate = "72";
        RespRate = "15";

        Saturation = "On Oxygen";
        OxygenSaturation = "50";
        RefererID = "829";
        FormID = "1";

        CaseCode = CASE_CODE;

        Weight = "0";
        WeightUnit = "kg";
        Height = "5";
        HeightUnit = "inches";
        PatientPainPresent = "No";
        PainPatient = "0";

        CityID = "1";
        Data = "headache";
        Diagnosis = "Corona";
        DiseaseID = "1";
        Gender = "f";
        History = "None";
        LocationID = "35";

        m_age = "3";    // Month
        PatientID = "0";

        UsersID = "829";

        Comment = "Malaria and Corona Virus";
        y_age = "20";  // Year
        created_at = "2020-07-21 1:30:37";

        // isPainPresent? Set to no
        setPainPatient("0");

    }

    private int toInt(String fromString, int errorValue) {

        int convertedValue = errorValue;

        try
        {
            // the String to int conversion happens here
            convertedValue = Integer.parseInt(fromString.trim());

            // print out the value after the conversion
            System.out.println("toInt int = " + convertedValue);
        }
        catch (NumberFormatException nfe)
        {
            System.out.println("toInt: " + nfe.getMessage());
        }

        return convertedValue;
    }

    // Set the patients Weight
    public void setWeight(String weight) { Weight = weight; }
    public String getWeight() { return Weight; }

    // Set the patients Weight units (KGs/LBs)
    public void setWeightUnit(String weightUnit) { this.WeightUnit = weightUnit; }
    public String getWeightUnit() { return this.WeightUnit; }

    public void setHeightUnit(String heightUnit) { HeightUnit = heightUnit; }
    public String getHeightUnit() { return HeightUnit; }

    // Set pain is Yes or No String
    public void setPatientPainPresent(String patientPainPresent) { PatientPainPresent = patientPainPresent; }
    public String getPatientPainPresent() { return PatientPainPresent; }

    public void setPainPatient(String painPatient) {

        this.PainPatient = painPatient;

        if(toInt(painPatient,0)>0)
        {
            setPatientPainPresent("Yes");
        } else {
            setPatientPainPresent("No");
        }
    }

    public String getPainPatient() { return PainPatient; }

    public void setCityID(String cityID) { CityID = cityID; }
    public String getCityID() { return CityID; }

    public void setDataString(String data) { Data = data; }
    public String getDataString() { return Data; }

    public void setDiagnosis(String diagnosis) { Diagnosis = diagnosis; }
    public String getDiagnosis() { return Diagnosis; }

    public void setDiseaseId(String diseaseId) { this.DiseaseID = diseaseId; }
    public String getDiseaseId() { return DiseaseID; }

    public void setGender(String gender) { Gender = gender; }
    public String getGender() { return Gender; }

    public void setHistory(String history) { History = history; }
    public String getHistory() { return History; }

    public void setLocationID(String locationID) { LocationID = locationID;}
    public String getLocationID() { return LocationID; }

    public void setM_age(String m_age) { this.m_age = m_age; }
    public String getM_age() { return m_age; }

    public void setPatientID(String patientID) { PatientID = patientID; }
    public String getPatientID() { return PatientID; }

    public void setUsersID(String usersID) { UsersID = usersID; }
    public String getUsersID() { return UsersID; }

    public void setComment(String comment) { Comment = comment; }
    public String getComment() { return Comment; }

    public void setY_age(String y_age) { this.y_age = y_age; }
    public String getY_age() { return y_age; }

    // Future option to enable or disable debug message for API
    public void enableDebug() { mDebugEnabled = true; }
    public void disableDebug() { mDebugEnabled = false; }


    private void setReturnString(String returnString) { retString = returnString; }
    public String getReturnString() { return retString; }

    // Getters for API Variables
    public String getRefererID() {
        return RefererID;
    }

    // Set the patient refer ID number
    public String setRefererID(String referer_ID)
    {
        if(!this.RefererID.isEmpty())
            this.RefererID = referer_ID;

        return this.RefererID;
    }

    public String getBloodPressurePosition() {
        return BloodPressurePosition;
    }

    public String getDiastolic() {
        return Diastolic;
    }

    public String getPatientFirstName() {
        return PatientFirstName;
    }

    public String getHands() {
        return Hands;
    }

    public String getPatientLastName() {
        return PatientLastName;
    }

    public String getOralTemperature() {
        return OralTemperature;
    }

    public String getPulsePres() {
        return PulsePres;
    }

    public String getSaturation() {
        return Saturation;
    }

    public static String getServerUri() {
        return SERVER_URI;
    }

    public String getSystolic() {
        return Systolic;
    }

    public String getTemperature() {
        return Temperature;
    }

    public String getTemperatureValue() {
        return TemperatureValue;
    }

    public String getHeartPulseRate() {
        return this.HeartPulseRate;
    }

    public String getRespRate() {
        return this.RespRate;
    }

    public String getOxygenSaturation() {
        return this.OxygenSaturation;
    }

    public void setFormID(String formID) {
        this.FormID = formID;
    }

    public String getFormID() {
        return this.FormID;
    }

    // CaseCode Update
    public void setCaseCode(String caseCode) {
        this.CaseCode = caseCode;
    }

    public String getCaseCode() {
        return this.CaseCode;
    }

    // Setters for API Variables
    public String setHeartRate(String heartRate)
    {
        if(heartRate != null && !heartRate.isEmpty())
        {
            this.HeartPulseRate = heartRate;
            return this.HeartPulseRate;
        }

        return this.HeartPulseRate;
    }

    public String setRespirationRate(String respRate)
    {
        if(respRate != null && !respRate.isEmpty())
         this.RespRate = respRate;

        return this.RespRate;
    }

    public String setOxygenSaturation(String oxygenSat)
    {
        if(oxygenSat != null && !oxygenSat.isEmpty())
            this.OxygenSaturation = oxygenSat;

        return oxygenSat;
    }


    // Date Code
    public void convertDateCode() {

        long timeInMilliseconds = 0;
        String date = "Apr 23 16:38:21 GMT+05:30 2019";
        DateTimeFormatter formatter = null;
        // Only works with API 26 and Higher
  /*
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            LocalDateTime localDate = LocalDateTime.parse(date, formatter);
            timeInMilliseconds = localDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();
        }
        */

        Log.d(TAG, "API Data: "+ android.os.Build.VERSION.SDK_INT);
        Log.d(TAG, "Date in milli :: FOR API >= 26 >>> " + timeInMilliseconds);
    }

/*
    public Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }
*/
    // Touch creation date (update to androids current date/time)
    public void touch()
    {
        setCreationDate();
    }

    public void setCreationDate()
    {
        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
        Date date=null;

        // Get current date/time from android device.
        date = new Date(System.currentTimeMillis());

        this.setCreationDate(date);
    }

    public String getCreationDate()
    {
        return created_at;
    }

    public boolean setCreationDate(Date creationDate)
    {
        boolean setStatus = false;
        // Set the proper pattern for the Date format
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {

            System.out.println("DATE: "+creationDate);
            System.out.println("Formated: "+dateFormat.format(creationDate));

            created_at = dateFormat.format(creationDate);

            Log.d(TAG,"CreateD: " + created_at);

            setStatus = true;
        }
        catch(Exception e) {
            //java.text.ParseException: Unparseable date: Geting error
            System.out.println("Excep"+e);

            Log.d(TAG,"Date Format Error!");

        }

        return setStatus;
    }

    // ********************************************************************
    // Build a Hash Map for Sending URL Encoded data for the POST Command
    //      Preform any PreAPI Transmission Pre-Req Needs
    //
    public HashMap<String, String> GetURLEncodeResults()
    {
      //  https://mdconsults.org/MDCAPI/public/createCaseVital?
        //  case_code=871
        //  form_id=1
        //  referer_id=829
        //  fname=alexa
        //  lname=jackson
        //  temperature=40
        //  temperaturevalue=%E2%84%83
        //  oraltemperature=Oral
        //  systolic=125
        //  diastolic=90
        //  hands=Left
        //  bloodpressureposition=Lying
        //  pulse=72
        //  pulsepres=Regular
        //  respiratoryrate=15
        //  saturation=95
        //  satoxygen=On

      //  https://mdconsults.org/MDCAPI/public/createCaseVital?
        // case_code=871&
        // form_id=1&
        // referer_id=829&
        // fname=kanwal&
        // lname=Khan&
        // temperature=40&
        // temperaturevalue=℃
        // &oraltemperature=Oral&
        // systolic=125&
        // diastolic=90&
        // hands=Left&
        // bloodpressureposition=Lying&pulse=72&pulsepres=Regular&respiratoryrate=15&saturation=95&satoxygen=Onoxygen&weight=48&weightunit=kg&height=5&heightunit=inches&painpatienty=4&painpatient=Yes&city=1&data=headache&diagnosis=corona&disease_id=1&gender=f&history=Typhoid&location_id=35&m_age=3&patient_id=0&users_id=829&comment=Malaria&y_age=20&created_at=2020-07-21 1:30:37

        // https://mdconsults.org/MDCAPI/public/createCaseVital?
        // case_code=871
        // form_id=1
        // referer_id=829
        // fname=kanwal
        // lname=Khan
        // temperature=40
        // temperaturevalue=℃
        // oraltemperature=Oral
        // systolic=125
        // diastolic=90
        // hands=Left
        // bloodpressureposition=Lying
        // pulse=72
        // pulsepres=Regular
        // respiratoryrate=15
        // saturation=95
        // satoxygen=Onoxygen
        // weight=48
        // weightunit=kg
        // height=5
        // heightunit=inches
        // painpatienty=4
        // painpatient=Yes
        // city=1
        // data=headache
        // diagnosis=corona
        // disease_id=1
        // gender=f
        // history=Typhoid
        // location_id=35
        // m_age=3
        // patient_id=0
        // users_id=829
        // comment=Malaria
        // y_age=20
        // created_at=2020-07-21 1:30:37
        
        HashMap<String, String> stringMap = new HashMap<>();

        // Build HashMap Value Pairs for Posted Data
        // Unique Case Code..  Should be updated, but no method of finding last used value
        stringMap.put("case_code",  CaseCode);

        // Fixed Value DNC
        stringMap.put("form_id", FormID);

        // Referrer ID (ID of the Doctor where the refer came from
        // Should be set to the same ID of the doc doing the reading for now
        stringMap.put("referer_id", RefererID);

        // Patient Name (First and Last)
        stringMap.put("fname", PatientFirstName);
        stringMap.put("lname", PatientLastName);

        // Temperature Details
        stringMap.put("temperature",Temperature);
        // In C or F
        stringMap.put("temperaturevalue",TemperatureValue);
        // Location Taken (Oral/etc)
        stringMap.put("oraltemperature",OralTemperature);

        // Blood Pressure Measurements
        stringMap.put("systolic",Systolic);
        stringMap.put("diastolic",Diastolic);
        stringMap.put("hands",Hands);
        stringMap.put("bloodpressureposition",BloodPressurePosition);

        // Pulse Rate
        stringMap.put("pulse",HeartPulseRate);

        // Respiratory rate
        stringMap.put("pulsepres",PulsePres);
        stringMap.put("respiratoryrate",RespRate);

        // Oxygen Saturation: 80-99 (%)
        stringMap.put("saturation",OxygenSaturation);
        // Oxygen Saturation Notes (Type: On Oxygen)
        stringMap.put("satoxygen",Saturation);
        
        // Other
        stringMap.put("weight",Weight);
        stringMap.put("weightunit",WeightUnit);
        stringMap.put("height",Height);
        stringMap.put("heightunit",HeightUnit);
        stringMap.put("painpatienty", PatientPainPresent);
        stringMap.put("painpatient",PainPatient);

        stringMap.put("city", CityID);

        stringMap.put("data",Data);
        stringMap.put("diagnosis",Diagnosis);
        stringMap.put("disease_id", DiseaseID);
        stringMap.put("gender",Gender);

        stringMap.put("history",History);
        stringMap.put("location_id",LocationID);
        stringMap.put("m_age",m_age);
        stringMap.put("patient_id",PatientID);
        stringMap.put("users_id",UsersID);
        stringMap.put("comment",Comment);
        stringMap.put("y_age",y_age);
        stringMap.put("created_at",created_at);

        // Build URL String for Request
        return (HashMap<String, String>) stringMap;
    }

    // Unused method to build
    // Secondary String builder for URLEncoded Data
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            // Variable Name
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            // Variable Data
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    // Reference: https://stackoverflow.com/questions/2938502/sending-post-data-in-android

    public String  performPostCall(String requestURL, HashMap<String, String> postDataParams) {

        URL url;
        String response = "";
        try {

            if(enableURLPadding)
                url = new URL(requestURL+"?"+MapQuery.urlEncodeUTF8(postDataParams));
            else
                url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            // Convert MAP Data into a Post Encoded String
            writer.write(MapQuery.urlEncodeUTF8(postDataParams));

            // Write and flush the post data buffer.
            writer.flush();
            writer.close();

            // Close the connection.
            os.close();

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG,"D: " + response);

        // Update the return status string
        setReturnString(response);

        return response;
    }

    public String getStatusMessage() {
/*
           JSONObject obj = new JSONObject(getReturnString());
                String pageName = obj.getJSONObject("pageInfo").getString("pageName");

           System.out.println(pageName);

           JSONArray arr = null;

        try {

            arr = obj.getJSONArray("posts");

            for (int i = 0; i < arr.length(); i++) {
                String post_id = arr.getJSONObject(i).getString("post_id");
                System.out.println(post_id);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        return "";
    }

    // Non-Blocking Call (Threaded)
    public void sendDataToTechForLifeAPIServerBackGround (final String jsonString) {

        // Required if multiple requests are fired at the same time.
        // Without results being external before the thread starts, values are overwritten
        // Only required when using a thread to send the results.
        final HashMap<String, String> copiedData = GetURLEncodeResults();

        // Threading Removed for debug and testing
        final Thread thread = new Thread(new Runnable() {

            //private boolean mDebugEnabled = this.mDebugEnabled;
            public void run() {
                try {

                    final String s = performPostCall(SERVER_URI, copiedData);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        thread.start();

        return;
    }

    // Thread Blocking Call
    public void sendDataToTechForLifeAPIServer (final String jsonString) {

        performPostCall(SERVER_URI, GetURLEncodeResults());

        return;
    }

    public void sendData(boolean enableThreading) {
        if(!enableThreading) {
            sendDataToTechForLifeAPIServer("");
        } else {
            sendDataToTechForLifeAPIServerBackGround("");
        }
    }

    public boolean setVariables() {



        return false;
    }

    // Return a json version of the hashMap
    public String toJson() {

//        HashMap<String, String> copiedData = new HashMap<>();

        HashMap<String, String> copiedData = GetURLEncodeResults();

        // Deep clone of data
        Gson gson = new Gson();
        String jsonString = gson.toJson(copiedData);

        Log.d(TAG,"Hash Mapped data:" + jsonString);


        /*
                Type type = new TypeToken<HashMap<Integer, Employee>>() {
        }.getType();
        HashMap<Integer, Employee> clonedMap = gson.fromJson(jsonString, type);

        System.out.println(clonedMap);

         */
        return jsonString;

    }
    public String loadWeb(String url, HashMap<String, String> post)  throws IOException {

        String result = "";
        try {
            String strPost = "";
            for(String key : post.keySet()){
                strPost+= key+"="+post.get(key)+"&";
            }
            strPost = strPost.substring(0,strPost.length()-1);

            URL urlPost = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection)urlPost.openConnection();

            // Setup Connection Method and Header Information
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
            urlConnection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
            // Set the option for output data
            urlConnection.setDoOutput(true);

            // Setup Data Stream for data delivery
            DataOutputStream dStream = new DataOutputStream(urlConnection.getOutputStream());
            dStream.writeBytes(strPost);
            dStream.flush();
            dStream.close();

            // Read the requested data
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line = "";
            StringBuilder responseOutput = new StringBuilder();

            while((line = br.readLine()) != null ) {
                responseOutput.append(line);
            }

            br.close();

            result = responseOutput.toString();

        } catch (Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

}
