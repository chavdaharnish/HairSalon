package com.example.hairsalon;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ImageViewHolder> {
    public static OrderClass uploadCurrent;
    private Context mContext,pContext;
    private OrderClass button;
    private List<OrderClass> mUploads;
    private OnItemClickListener mListener;
    String accept = null;
    String getName=null,getEmail=null,getDT=null,getHairstyle=null,getPrice=null;

    public CustomAdapter(Context context,Context pcontext, List<OrderClass> uploads) {
        mContext = context;
        mUploads = uploads;
        pContext = pcontext;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.activity_listview_of_orders, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        uploadCurrent = mUploads.get(position);

        //Upload uploadCurrent = mUploads.get(position);
        holder.orderName.setText(uploadCurrent.getCustomerName());
        holder.orderEmail.setText(uploadCurrent.getCustomerEmail());
        holder.orderDT.setText(uploadCurrent.getDateAndTime());
        holder.orderPrice.setText(uploadCurrent.getPrice());
        holder.orderHairstyleName.setText(uploadCurrent.getHairStyle());

        button = mUploads.get(position);
        final String status = button.getCustomerName();
        final String getEmail = button.getCustomerEmail();
        final String getDT = button.getDateAndTime();
        final String getHairstyle = button.getHairStyle();
        final String getPrice = button.getPrice();

        if(Main3Activity.value!=null && Main3Activity.value.equals("request")) {
            holder.orderAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(status!=null) {
                        /*name = status;
                        email = button.getCustomerEmail();
                        dateandtime = button.getDateAndTime();*/
                        DatabaseReference databaseReference;
                        databaseReference = FirebaseDatabase.getInstance().getReference("HAIRSTYLIST")
                                .child(Main3Activity.key)
                                .child("Requests")
                                .child(status);
                        HashMap map = new HashMap();
                        map.put("statusofBooking", "Accepted");
                        databaseReference.updateChildren(map);
                        accept = "\nhas been accepted by hairstylist";
                        sendMail(getEmail,status,getDT,getHairstyle,getPrice);
                        Toast.makeText(pContext, "Accepted", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.orderDeny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(status !=null) {
                        /*name = status;
                        email = button.getCustomerEmail();
                        dateandtime = button.getDateAndTime();*/
                        DatabaseReference databaseReference;
                        databaseReference = FirebaseDatabase.getInstance().getReference("HAIRSTYLIST")
                                .child(Main3Activity.key)
                                .child("Requests")
                                .child(status);
                        HashMap map = new HashMap();
                        map.put("statusofBooking", "Denied");
                        databaseReference.updateChildren(map);
                        accept = "\nhas been denied by hairstylist due to some private reason\nsorry for negative response\n";
                        sendMail(getEmail,status,getDT,getHairstyle,getPrice);

                        Toast.makeText(pContext, "Denied", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if(Main3Activity.value!=null && Main3Activity.value.equals("order")) {

            holder.orderCancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (status != null) {
                        /*name = status;
                        email = button.getCustomerEmail();
                        dateandtime = button.getDateAndTime();*/
                        DatabaseReference databaseReference;
                        databaseReference = FirebaseDatabase.getInstance().getReference("HAIRSTYLIST")
                                .child(Main3Activity.key)
                                .child("Requests")
                                .child(status);
                        HashMap map = new HashMap();

                        map.put("statusofBooking", "Cancelled");
                        databaseReference.updateChildren(map);
                        accept = "\nhas been cancelled by hairstylist due to some private reason\nsorry for negative response\n";
                        sendMail(getEmail,status,getDT,getHairstyle,getPrice);
                        Toast.makeText(pContext, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView orderName,orderEmail,orderDT,orderHairstyleName,orderPrice;
        Button orderAccept,orderDeny,orderCancle;

        public ImageViewHolder(View itemView) {
            super(itemView);

            orderName = itemView.findViewById(R.id.orderName);
            orderEmail = itemView.findViewById(R.id.orderEmail);
            orderDT = itemView.findViewById(R.id.orderDT);
            orderAccept = itemView.findViewById(R.id.orderAccept);
            orderDeny = itemView.findViewById(R.id.orderDeny);
            orderCancle = itemView.findViewById(R.id.orderCancle);
            orderHairstyleName = itemView.findViewById(R.id.orderHairstylename);
            orderPrice = itemView.findViewById(R.id.orderPrice);

            if(Main3Activity.value.equals("order")){
                orderAccept.setVisibility(View.INVISIBLE);
                orderDeny.setVisibility(View.INVISIBLE);
                orderCancle.setVisibility(View.VISIBLE);
            }
            if(Main3Activity.value.equals("request")){
                orderAccept.setVisibility(View.VISIBLE);
                orderDeny.setVisibility(View.VISIBLE);
                orderCancle.setVisibility(View.INVISIBLE);
            }
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem doWhatever = menu.add(Menu.NONE, 1, 1, "Do whatever");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");

            doWhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {
                        case 1:
                            mListener.onWhatEverClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onWhatEverClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public void sendMail(String customerEmail, String status, String dt , String hairStyle, String price ) {

        getName = status;
        getEmail = customerEmail;
        getDT = dt;
        getHairstyle = hairStyle;
        getPrice = price;

        OrderMail orderMail = new OrderMail(pContext ,customerEmail);
        orderMail.execute();
    }


    class OrderMail extends AsyncTask<Void,Void,Void> {

        //Add those line in dependencies
        //implementation files('libs/activation.jar')
        //implementation files('libs/additionnal.jar')
        //implementation files('libs/mail.jar')

        //Need INTERNET permission

        //Variables
        private Context mContext;
        private Session mSession;

        private String mEmail;
        private String mSubject;
        private String mMessage;

        private ProgressDialog mProgressDialog;

        //Constructor
        public OrderMail(Context mContext, String mEmail) {
            this.mContext = mContext;
            this.mEmail = mEmail;
            this.mSubject = mSubject;
            this.mMessage = mMessage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Show progress dialog while sending email
            mProgressDialog = ProgressDialog.show(mContext,"Sending message", "Please wait...",false,false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //Dismiss progress dialog when message successfully send
            mProgressDialog.dismiss();

            //Show success toast
            Toast.makeText(mContext,"Message Sent",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Creating properties
            Properties props = new Properties();

            //Configuring properties for gmail
            //If you are not using gmail you may need to change the values
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            //Creating a new session
            mSession = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        //Authenticating the password
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(Utils.EMAIL, Utils.PASSWORD);
                        }
                    });

            try {
                //Creating MimeMessage object
                MimeMessage mm = new MimeMessage(mSession);

                //Setting sender address
                mm.setFrom(new InternetAddress(Utils.EMAIL));
                //Adding receiver
                mm.addRecipient(Message.RecipientType.TO, new InternetAddress(mEmail));
                //Adding subject
                mm.setSubject("Regarding Your Order");
                //Adding message
                mm.setText("Hello dear,\nHairstylist Name:-" + Main3Activity.fname
                        +"\nHairstylist Mobile No. :- " + Main3Activity.mobile
                        +"\nHairstylist Email:-" + Main3Activity.email
                        +"\nAddress of Saloon:- " + Main3Activity.address
                        +"\n------YOUR BOOKING DETAILS-------"
                        +"\nYour Hair Style:- " + getHairstyle
                        +"\nPrice:- " + getPrice
                        +"\nDate and Time:- " + getDT
                        + "\n" + accept
                        +"\n\nThanks & Regards,\nMr. Cut");

                //Sending email
                Transport.send(mm);

//            BodyPart messageBodyPart = new MimeBodyPart();
//
//            messageBodyPart.setText(message);
//
//            Multipart multipart = new MimeMultipart();
//
//            multipart.addBodyPart(messageBodyPart);
//
//            messageBodyPart = new MimeBodyPart();
//
//            DataSource source = new FileDataSource(filePath);
//
//            messageBodyPart.setDataHandler(new DataHandler(source));
//
//            messageBodyPart.setFileName(filePath);
//
//            multipart.addBodyPart(messageBodyPart);

//            mm.setContent(multipart);

            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}


