package ru.comtrans.items;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;


public class User implements Parcelable {

    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("err_code")
    @Expose
    private int errCode;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("companyPosition")
    @Expose
    private String companyPosition;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;

    /**
     * No args constructor for use in serialization
     *
     */
    public User() {
    }

    public User(String email) {
        this.email = email;

    }

    public User(String email,String password) {
        this.email = email;
        this.password = password;
    }

    public User(String firstName,String lastName,String company,String companyPosition,String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.companyPosition = companyPosition;
        this.phone = phone;
    }

    /**
     *
     * @param message
     * @param lastName
     * @param phone
     * @param email
     * @param token
     * @param status
     * @param companyPosition
     * @param company
     * @param errCode
     * @param firstName
     * @param password
     */
    public User(int status, String message, int errCode, String firstName, String lastName, String token, String company, String companyPosition, String phone, String email,String password) {
        this.status = status;
        this.message = message;
        this.errCode = errCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.token = token;
        this.company = company;
        this.companyPosition = companyPosition;
        this.phone = phone;
        this.email = email;
    }

    /**
     *
     * @return
     * The status
     */
    public int getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    public User withStatus(int status) {
        this.status = status;
        return this;
    }

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public User withMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     *
     * @return
     * The errCode
     */
    public int getErrCode() {
        return errCode;
    }

    /**
     *
     * @param errCode
     * The err_code
     */
    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public User withErrCode(int errCode) {
        this.errCode = errCode;
        return this;
    }

    /**
     *
     * @return
     * The firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName
     * The firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public User withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    /**
     *
     * @return
     * The lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName
     * The lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public User withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    /**
     *
     * @return
     * The token
     */
    public String getToken() {
        return token;
    }

    /**
     *
     * @param token
     * The token
     */
    public void setToken(String token) {
        this.token = token;
    }

    public User withToken(String token) {
        this.token = token;
        return this;
    }

    /**
     *
     * @return
     * The company
     */
    public String getCompany() {
        return company;
    }

    /**
     *
     * @param company
     * The company
     */
    public void setCompany(String company) {
        this.company = company;
    }

    public User withCompany(String company) {
        this.company = company;
        return this;
    }

    /**
     *
     * @return
     * The companyPosition
     */
    public String getCompanyPosition() {
        return companyPosition;
    }

    /**
     *
     * @param companyPosition
     * The companyPosition
     */
    public void setCompanyPosition(String companyPosition) {
        this.companyPosition = companyPosition;
    }

    public User withCompanyPosition(String companyPosition) {
        this.companyPosition = companyPosition;
        return this;
    }

    /**
     *
     * @return
     * The phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     *
     * @param phone
     * The phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public User withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    /**
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public User withEmail(String email) {
        this.email = email;
        return this;
    }

    /**
     *
     * @return
     * The password
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password
     * The email
     */
    public void setPassword(String password) {
        this.email = email;
    }

    public User withPassword(String password) {
        this.email = email;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status);
        dest.writeString(this.message);
        dest.writeInt(this.errCode);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.token);
        dest.writeString(this.company);
        dest.writeString(this.companyPosition);
        dest.writeString(this.phone);
        dest.writeString(this.email);
        dest.writeString(this.password);
    }

    protected User(Parcel in) {
        this.status = in.readInt();
        this.message = in.readString();
        this.errCode = in.readInt();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.token = in.readString();
        this.company = in.readString();
        this.companyPosition = in.readString();
        this.phone = in.readString();
        this.email = in.readString();
        this.password = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
