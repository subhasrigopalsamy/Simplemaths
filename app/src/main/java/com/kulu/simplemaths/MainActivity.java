package com.kulu.simplemaths;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView result;
    EditText input;
    Button btnCalc, btnClear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input = (EditText) findViewById(R.id.input);        // input expression edittext
        result = (TextView) findViewById(R.id.result);      // result textview
        btnClear = (Button) findViewById(R.id.btnClear);    // clear button - clears input & result
        btnClear.setOnClickListener(this);
        btnCalc = (Button) findViewById(R.id.btnCalc);      // calc button - triggers expression evaluation
        btnCalc.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btnCalc:
                if(input.getText().toString().isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Empty Expression" , Toast.LENGTH_SHORT).show();
                }
                else {
                    Double resultvalue = eval(input.getText().toString());
                    String result1 = Double.toString(round(resultvalue, 4));  // round off decimals to 4 digits
                    result.setText(result1);
                }
                break;

            case R.id.btnClear:
                input.setText("");
                result.setText("");
                break;

        }
    }


    public double eval(final String str) {
        class Parser {
            int pos = -1, c;

            void nextChar() {
                c = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            //if whitespace fetch next char
            void isSpace() {
                while (Character.isWhitespace(c)) nextChar();
            }

            double parse() {
                nextChar();
                double v = parseExpression();
                if (c != -1) {
                    result.setText("");
                    Toast.makeText(MainActivity.this, "Unrecognized Expression  "+ (char)c , Toast.LENGTH_SHORT).show();
                    v=0;
                    // throw new RuntimeException("Unexpected: " + (char)c);
                }

                return v;
            }

            //parseExpression - for + and -
            //parseTerm - for * and /
            //parseFactor - for numbers


            double parseExpression() {
                double v = parseTerm();
                for (;;) {
                    isSpace();
                    if (c == '+') { // addition
                        nextChar();
                        v += parseTerm();
                    } else if (c == '-') { // subtraction
                        nextChar();
                        v -= parseTerm();
                    } else {
                        return v;
                    }
                }
            }

            double parseTerm() {
                double v = parseFactor();
                for (;;) {
                    isSpace();
                    if (c == '/') { // division
                        nextChar();
                        v /= parseFactor();
                    } else if (c == '*') { // multiplication
                        nextChar();
                        v *= parseFactor();
                    } else {
                        return v;
                    }
                }
            }

            double parseFactor() {
                double v ;
                isSpace();
                     // numbers
                    StringBuilder sb = new StringBuilder();
                    while ((c >= '0' && c <= '9')) {
                        sb.append((char)c);
                        nextChar();
                    }
                    if (sb.length() == 0)
                    {
                        result.setText("");
                        Toast.makeText(MainActivity.this, "Unrecognized Expression  "+(char)c , Toast.LENGTH_SHORT).show();
                        v = 0;
                       // throw new RuntimeException("Unexpected: " + (char)c);
                    }
                    else {
                        v = Double.parseDouble(sb.toString());
                    }
                isSpace();
                return v;
            }
        }
        return new Parser().parse();
    }

    //rounding values
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
