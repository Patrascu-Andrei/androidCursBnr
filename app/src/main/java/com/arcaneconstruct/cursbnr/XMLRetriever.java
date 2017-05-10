package com.arcaneconstruct.cursbnr;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dragos on 7/25/2015.
 * AsyncTask to retrieve xml from web location
 */
public class XMLRetriever extends AsyncTask<Void,Void,List<Curs>>{
    private static final String ns = null;
    private static final  java.lang.String TAG ="XMLRetriever" ;
    private String date;
    public static final String BODY_TAG = "Body";
    public static final String DATASET_TAG = "DataSet";
    public static final String DATE_TAG = "Cube";
    public static final String DATE_ATT = "date";
    public static final String RATE_TAG = "Rate";
    public static final String CURRENCY_ATT = "currency";
    URL url;;
    Context context;

    /**
     * Constructor
     * @param context
     */
    public XMLRetriever(Context context) {
        this.context = context;
    }

    /**
     * Face toate procesarile in fir de executie separat si returneaza lista de cursuri de schimb
     * @param params
     * @return {@code List<Curs>}
     */
    @Override
    protected List<Curs> doInBackground(Void... params) {
        try {
            Log.d(TAG,"read url");
            url = new URL("http://bnr.ro/nbrfxrates.xml");
            return  parseXML(getInputStream(url));

        }catch (MalformedURLException me){
            me.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Metoda executata cand se finalizeaza procesarile pe thread-ul separat
     * @param result
     */
    @Override
    protected void onPostExecute(List<Curs> result)
    {
        Log.d(TAG,"PostExecute");
        ((MainActivity) context).setMyListData(result);
    }

    /**
     * Parseaza un xml primit ca si InputStream si incepe citirea datelor returnand rezultatul citirilor
     * @param in
     * @return {@code List<Curs>}
     * @throws XmlPullParserException
     * @throws IOException
     */
    public List<Curs> parseXML(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readData(parser);
        } finally {
            in.close();
        }

    }

    /**
     * Citeste datele din xml returneaza rezultatul procesarii tag-ului Body sub forma de lista de Cursuri de schimb
     * @param parser
     * @return {@code List<Curs>}
     * @throws IOException
     * @throws XmlPullParserException
     */
    private List<Curs> readData(XmlPullParser parser) throws IOException, XmlPullParserException {
        //e obligatoriu sa inceapa cu tag DataSet
        parser.require(XmlPullParser.START_TAG, ns, DATASET_TAG);
        while (parser.next()!=XmlPullParser.END_TAG) {//atat vreme cat nu intalnim sfarsitul documentului citim din el
            //ne intereseaza sa gasim tag-ul Body
            if (parser.getEventType() != XmlPullParser.START_TAG) {//nu ne intereseaza alt continut
                String name = parser.getName();
                continue;
            }
            String name = parser.getName();
            //Log.d(TAG,"read name"+name);
            if(name.equals(BODY_TAG)){
                //apelam metoda de tratare a tag-ului Body
                return readBody(parser);
            } else {
                //renuntam sa procesam alte tag-uri
                skip(parser);
            }
      }
        return null;
    }
    /**
     * Citeste continutul tag-ului Body si cauta in el tag-urile pentru data si curs de schimb
     * @param parser
     * @return {@code List<Curs>}
     * @throws IOException
     * @throws XmlPullParserException
     */
    private List<Curs> readBody(XmlPullParser parser) throws IOException, XmlPullParserException {
        List rates = new ArrayList();
        while (parser.next() != XmlPullParser.END_TAG) {
            //atat vreme cat nu intalnim sfarsitul Body citim din el
            //ne intereseaza sa gasim tag-ul Cube
            if (parser.getEventType() != XmlPullParser.START_TAG) {//nu ne intereseaza alt continut
                String name = parser.getName();
                Log.d(TAG, "read name" + name);
                continue;
            }
            String name = parser.getName();
            Log.d(TAG, "read name" + name);
            if (name.equals(DATE_TAG)) {
                readXMLDate(parser);
            } else if (name.equals(RATE_TAG)) {
                //avem un singur tag de data putem procesa si asa .. altfel trebuie inclus in readXMLDate
                rates.add(readRate(parser));
            } else {
                //nu ne intereseaza altceva
                skip(parser);
            }


        }
        return rates;
    }

    /**
     * Citeste data pentru cursul valutar
     * @param parser
     */
    private void readXMLDate(XmlPullParser parser ) {
        date = parser.getAttributeValue(null, DATE_ATT);
        Log.d(TAG,"read Date"+date);
    }

    /**
     * Citeste rata de schimb din tag-ul Rate
     * @param parser
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private Curs readRate(XmlPullParser parser) throws IOException, XmlPullParserException {
        Curs curs=new Curs();
        curs.setDate(date);
        curs.setCurrency(parser.getAttributeValue(null, CURRENCY_ATT));
        curs.setRate(readText(parser));
        Log.d(TAG, curs.toString());
        return curs;
    }

    /**
     * CIteste text-ul unui tag
     * @param parser
     * @return String
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /**
     * Sare peste procesarea continutului unui Tag
     * @param parser
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.d(TAG,"skip"+parser.getName());
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            //citeste urmatorul eveniment pana la inchiderea tag-ului initial
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    Log.d(TAG,"skip END TAG");
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    Log.d(TAG,"skip START TAG");
                    break;
            }
        }
    }

    /**
     * Metoda utilitara pentru obtinerea unui {@code InputStream} corespunzator fisierului xml
     * @param url
     * @return {@code InputStream}
     */
    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }
}
