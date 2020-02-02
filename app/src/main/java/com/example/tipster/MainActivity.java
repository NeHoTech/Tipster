package com.example.tipster;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    private EditText txtAmount;
    private EditText txtPeople;
    private EditText txtTipOther;

    private RadioGroup rdoGroupTips;
    private Button btnCalculate;
    private Button btnReset;
    String text;

    private TextView txtTipAmount;
    private TextView txtTotalToPay;
    private TextView txtTipPerPerson;

    // for the id of the selected radio button
    private int radioCheckedId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtAmount = (EditText)findViewById(R.id.txtAmount);
        txtAmount.requestFocus();
        txtPeople = (EditText)findViewById(R.id.txtPeople);
        txtTipOther = (EditText)findViewById(R.id.txtTipOther);
        rdoGroupTips = (RadioGroup)findViewById(R.id.radioGroupTips);
        btnCalculate = (Button)findViewById(R.id.btnCalculate);
        btnReset = (Button)findViewById(R.id.btnReset);
        txtTipAmount = (TextView)findViewById(R.id.txtTipAmount);
        txtTotalToPay = (TextView)findViewById(R.id.txtTotalPay);
        txtTipPerPerson = (TextView)findViewById(R.id.txtTipPerPerson);

        btnCalculate.setEnabled(false); // disable the calculate button on load
        txtTipOther.setEnabled(false); //disable the text field on load

        /* attach an OnCheckChangeListener to the radio group
           to monitor wich radio button is selected
         */
        rdoGroupTips.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // disable the other tip field if the 15% or the 20% button is selected
                if(checkedId == R.id.radioFifteen || checkedId == R.id.radioTwenty)
                {
                    txtTipOther.setEnabled(false);

                    // if there are valid inputs in the txtAmount and txtPeople, enable btnCalculate
                    btnCalculate.setEnabled(txtAmount.getText().length() > 0 &&
                            txtPeople.getText().length() > 0);
                }

                if(checkedId == R.id.radioOther)
                {
                    txtTipOther.setEnabled(true);
                    txtTipOther.requestFocus();

                    btnCalculate.setEnabled(txtAmount.getText().length() > 0 &&
                            txtPeople.getText().length() > 0 &&
                            txtTipOther.getText().length() > 0);
                }

                radioCheckedId = checkedId;
            }
        });

        txtAmount.setOnKeyListener(mKeyListener);
        txtPeople.setOnKeyListener(mKeyListener);
        txtTipOther.setOnKeyListener(mKeyListener);

        btnCalculate.setOnClickListener(mClickListener);
        btnReset.setOnClickListener(mClickListener);
    }

    /* KeyListener for txtAmount, txtPeople & txtTipOther.
     */
    private View.OnKeyListener mKeyListener = new View.OnKeyListener()
    {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event)
        {
            switch(v.getId())
            {
                case R.id.txtAmount:
                case R.id.txtPeople:
                    btnCalculate.setEnabled(txtAmount.getText().length() > 0 &&
                            txtPeople.getText().length() > 0);
                    break;
                case R.id.txtTipOther:
                    btnCalculate.setEnabled(txtAmount.getText().length() > 0 &&
                            txtPeople.getText().length() > 0 &&
                            txtTipOther.getText().length() > 0);
            }
            return false;
        }
    };

    private View.OnClickListener mClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if(v.getId() == R.id.btnCalculate)
            {
                calculate();
            }
            else
            {
                reset();
            }
        }
    };

    public void reset()
    {
        txtTipAmount.setText("");
        txtTipOther.setText("");
        txtPeople.setText("");
        txtAmount.setText("");
        txtTipPerPerson.setText("");
        txtTotalToPay.setText("");

        rdoGroupTips.clearCheck();
        rdoGroupTips.check(R.id.radioFifteen);

        txtAmount.requestFocus();
    }

    private void calculate()
    {
        Double billAmount = Double.parseDouble(txtAmount.getText().toString());
        Double totalPeople = Double.parseDouble(txtPeople.getText().toString());
        Double percentage = null;
        boolean isError = false;

        if(billAmount < 1.0)
        {
            showErrorAlert("Enter a valid amount.", txtAmount.getId());
            isError = true;
        }

        if(totalPeople < 1.0 )
        {
            showErrorAlert("Enter a valid value for No. of people.", txtPeople.getId());
            isError = true;
        }

        if(radioCheckedId == -1)
        {
            radioCheckedId = rdoGroupTips.getCheckedRadioButtonId();
        }

        if(radioCheckedId == R.id.radioFifteen)
        {
            percentage = 15.00;
        }
        else if(radioCheckedId == R.id.radioTwenty)
        {
            percentage = 20.00;
        }
        else if(radioCheckedId == R.id.radioOther)
        {
            percentage = Double.parseDouble(txtTipOther.getText().toString());
            if(percentage < 1.0)
            {
                showErrorAlert("Enter a valid tip percentage", txtTipOther.getId());
                isError = true;
            }
        }

        if(!isError)
        {
            Double tipAmount = (billAmount * percentage / 100);
            Double totalToPay = billAmount + tipAmount;
            Double perPersonPays = totalToPay / totalPeople;

            txtTipAmount.setText(tipAmount.toString());
            txtTotalToPay.setText(totalToPay.toString());
            txtTipPerPerson.setText(perPersonPays.toString());
        }
    }

    /**
     * Show the error message
     * @param errorMessage
     *      String for the message
     * @param fieldId
     *      Id of the field so we can focus on it
     */
    private void showErrorAlert(String errorMessage, final int fieldId)
    {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Error");
        alertBuilder.setMessage(errorMessage);
        alertBuilder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                findViewById(fieldId).requestFocus();
            }
        });
        alertBuilder.show();
    }
}
