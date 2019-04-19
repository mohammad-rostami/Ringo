package com.isatel.app.ringo;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;


public class Webservice {
    EditText txt;
    TextView part1;
    TextView part2;
    String data;
    String[] separated;
    private ProgressDialog progress;

    static class GetData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String linkType = urls[0];
            HttpURLConnection urlConnection = null;
            String result = "";
            try {
                URL url = new URL(linkType);
                urlConnection = (HttpURLConnection) url.openConnection();

                int code = urlConnection.getResponseCode();

                if (code == 200) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    if (in != null) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                        String line = "";

                        while ((line = bufferedReader.readLine()) != null)
                            result += line;
                    }
                    in.close();
                }

                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return result;
        }
    }

    static class PostClass extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String linkType = urls[0];
            String firstPart = urls[1];
            String post_result = null;

            try {
                URL url = new URL(linkType);
                post_result = "";
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                Map<String, Object> params1 = new LinkedHashMap<>();
                params1.put("pass", firstPart);
//                params1.put(firstPart, secondPart);

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params1.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                String urlParameters = postData.toString();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setDoOutput(true);
//                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
//                dStream.writeBytes(urlParameters.toString());
//                dStream.flush();
//                dStream.close();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                bw.write(urlParameters.toString());
                bw.flush();
                bw.close();
                int responseCode = connection.getResponseCode();

                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post parameters : " + urlParameters.toString());
                System.out.println("Response Code : " + responseCode);

                final StringBuilder output = new StringBuilder("Request URL " + url);
                output.append(System.getProperty("line.separator") + "Request Parameters " + urlParameters.toString());
                output.append(System.getProperty("line.separator") + "Response Code " + responseCode);
                output.append(System.getProperty("line.separator") + "Type " + "POST");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                System.out.println("output===============" + br);
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                    post_result += line;
                }
                br.close();

                output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + System.getProperty("line.separator") + responseOutput.toString());


            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return post_result;
        }


    }
    static class PostClassCategory extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String linkType = urls[0];
            String firstPart = urls[1];
            String secondtPart = urls[2];
            String post_result = null;

            try {
                URL url = new URL(linkType);
                post_result = "";
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                Map<String, Object> params1 = new LinkedHashMap<>();
                params1.put("pass", firstPart);
                params1.put("cat", secondtPart);
//                params1.put(firstPart, secondPart);

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params1.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                String urlParameters = postData.toString();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setDoOutput(true);
//                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
//                dStream.writeBytes(urlParameters.toString());
//                dStream.flush();
//                dStream.close();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                bw.write(urlParameters.toString());
                bw.flush();
                bw.close();
                int responseCode = connection.getResponseCode();

                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post parameters : " + urlParameters.toString());
                System.out.println("Response Code : " + responseCode);

                final StringBuilder output = new StringBuilder("Request URL " + url);
                output.append(System.getProperty("line.separator") + "Request Parameters " + urlParameters.toString());
                output.append(System.getProperty("line.separator") + "Response Code " + responseCode);
                output.append(System.getProperty("line.separator") + "Type " + "POST");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                System.out.println("output===============" + br);
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                    post_result += line;
                }
                br.close();

                output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + System.getProperty("line.separator") + responseOutput.toString());


            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return post_result;
        }


    }

    static class Notification extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String linkType = urls[0];
            String firstPart = urls[1];
            String secondPart = urls[2];
//            String secondPart = urls[2];
//            String thirdPart = urls[3];
//            String Url = null;
//            if (firstPart.equals("notif")) {
//                Url = "http://www.20decor.com/index.php?route=apiv1/notification";
//            } else if (firstPart != "notif") {
////                    Url = Splash_Screen.routs.getString(linkType);
//            }
//            String link = linkType + firstPart;


//            Integer thirdConvert = Integer.parseInt(thirdPart);
            String post_result = null;

            try {
                URL url = new URL(linkType);
                post_result = "";
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                Map<String, Object> params1 = new LinkedHashMap<>();
                params1.put("fcm_token", firstPart);
                params1.put("device_id", secondPart);

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params1.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                String urlParameters = postData.toString();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setDoOutput(true);
//                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
//                dStream.writeBytes(urlParameters.toString());
//                dStream.flush();
//                dStream.close();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                bw.write(urlParameters.toString());
                bw.flush();
                bw.close();
                int responseCode = connection.getResponseCode();

                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post parameters : " + urlParameters.toString());
                System.out.println("Response Code : " + responseCode);

                final StringBuilder output = new StringBuilder("Request URL " + url);
                output.append(System.getProperty("line.separator") + "Request Parameters " + urlParameters.toString());
                output.append(System.getProperty("line.separator") + "Response Code " + responseCode);
                output.append(System.getProperty("line.separator") + "Type " + "POST");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                System.out.println("output===============" + br);
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                    post_result += line;
                }
                br.close();

                output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + System.getProperty("line.separator") + responseOutput.toString());


            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return post_result;
        }


    }

    static class FilterPostClass extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String link = urls[0];
            String strLocation = urls[1];
            String strSaleType = urls[2];
            String strHouseType = urls[3];
            String strHouseTools = urls[4];
            String strColdTools = urls[5];
            String strHeatTools = urls[6];
            String strPrice = urls[7];
            String strSize = urls[8];

            String post_result = null;

            try {
                URL url = new URL(link);
                post_result = "";
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                Map<String, Object> params1 = new LinkedHashMap<>();
                try {
                    if (strLocation.length() > 0) {
                        if (!strLocation.equals("null")) {
                            params1.put("ads[location_area]", strLocation);
                        }
                    }
                } catch (Exception v) {
                }
                try {
                    if (strSaleType.length() > 0) {
                        if (!strSaleType.equals("null")) {
                            params1.put("ads[house_sale_type]", strSaleType);
                        }
                    }
                } catch (Exception v) {
                }
                try {
                    if (strHouseType.length() > 0) {
                        if (!strHouseType.equals("null")) {
                            params1.put("ads[house_type]", strHouseType);
                        }
                    }
                } catch (Exception v) {
                }
                try {
                    if (strHouseTools.length() > 0) {
                        if (!strHouseTools.equals("null")) {
                            params1.put("ads[house_tools]", strHouseTools);
                        }
                    }
                } catch (Exception v) {
                }
                try {
                    if (strColdTools.length() > 0) {
                        if (!strColdTools.equals("null")) {
                            params1.put("filter[][house_temperature_cold]", strColdTools);
                        }
                    }
                } catch (Exception v) {
                }
                try {
                    if (strHeatTools.length() > 0) {
                        if (!strHeatTools.equals("null")) {
                            params1.put("filter[][house_temperature_heat]", strHeatTools);
                        }
                    }
                } catch (Exception v) {
                }
                try {
                    if (strPrice.length() > 0) {
                        params1.put("ads[price]", strPrice);
                    }
                } catch (Exception v) {
                }
                try {
                    if (strSize.length() > 0) {
                        params1.put("ads[house_total_size]", strSize);
                    }
                } catch (Exception v) {
                }
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params1.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                String urlParameters = postData.toString();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setDoOutput(true);
//                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
//                dStream.writeBytes(urlParameters.toString());
//                dStream.flush();
//                dStream.close();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                bw.write(urlParameters.toString());
                bw.flush();
                bw.close();
                int responseCode = connection.getResponseCode();

                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post parameters : " + urlParameters.toString());
                System.out.println("Response Code : " + responseCode);

                final StringBuilder output = new StringBuilder("Request URL " + url);
                output.append(System.getProperty("line.separator") + "Request Parameters " + urlParameters.toString());
                output.append(System.getProperty("line.separator") + "Response Code " + responseCode);
                output.append(System.getProperty("line.separator") + "Type " + "POST");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                System.out.println("output===============" + br);
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                    post_result += line;
                }
                br.close();

                output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + System.getProperty("line.separator") + responseOutput.toString());


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return post_result;
        }


    }


    static class TourFilterPostClass extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String link = urls[0];
            String strSource = urls[1];
            String strTarget = urls[2];
            String strHouseType = urls[3];
            String strHouseTools = urls[4];
            String strColdTools = urls[5];
            String strHeatTools = urls[6];
            String strPrice = urls[7];
            String strSize = urls[8];

            String post_result = null;

            try {
                URL url = new URL(link);
                post_result = "";
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                Map<String, Object> params1 = new LinkedHashMap<>();
                try {
                    params1.put("ads[ads_type]", "3");
                } catch (Exception v) {
                }
                try {
                    if (strSource.length() > 0) {
                        if (!strSource.equals("null")) {
                            params1.put("ads[tour_source_city]", strSource);
                        }
                    }
                } catch (Exception v) {
                }
                try {
                    if (strTarget.length() > 0) {
                        if (!strTarget.equals("null")) {
                            params1.put("ads[tour_target_city]", strTarget);
                        }
                    }
                } catch (Exception v) {
                }
                try {
                    if (strHouseType.length() > 0) {
                        if (!strHouseType.equals("null")) {
                            params1.put("ads[house_type]", strHouseType);
                        }
                    }
                } catch (Exception v) {
                }
                try {
                    if (strHouseTools.length() > 0) {
                        params1.put("ads[house_tools]", strHouseTools);
                    }
                } catch (Exception v) {
                }
                try {
                    if (strColdTools.length() > 0) {
                        params1.put("filter[][house_temperature_cold]", strColdTools);
                    }
                } catch (Exception v) {
                }
                try {
                    if (strHeatTools.length() > 0) {
                        params1.put("filter[][house_temperature_heat]", strHeatTools);
                    }
                } catch (Exception v) {
                }
                try {
                    if (strPrice.length() > 0) {
                        params1.put("ads[price]", strPrice);
                    }
                } catch (Exception v) {
                }
                try {
                    if (strSize.length() > 0) {
                        params1.put("ads[house_total_size]", strSize);
                    }
                } catch (Exception v) {
                }
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params1.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                String urlParameters = postData.toString();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setDoOutput(true);
//                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
//                dStream.writeBytes(urlParameters.toString());
//                dStream.flush();
//                dStream.close();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                bw.write(urlParameters.toString());
                bw.flush();
                bw.close();
                int responseCode = connection.getResponseCode();

                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post parameters : " + urlParameters.toString());
                System.out.println("Response Code : " + responseCode);

                final StringBuilder output = new StringBuilder("Request URL " + url);
                output.append(System.getProperty("line.separator") + "Request Parameters " + urlParameters.toString());
                output.append(System.getProperty("line.separator") + "Response Code " + responseCode);
                output.append(System.getProperty("line.separator") + "Type " + "POST");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                System.out.println("output===============" + br);
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                    post_result += line;
                }
                br.close();

                output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + System.getProperty("line.separator") + responseOutput.toString());


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return post_result;
        }


    }

}
