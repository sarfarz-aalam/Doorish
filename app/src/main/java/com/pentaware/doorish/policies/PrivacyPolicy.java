package com.pentaware.doorish.policies;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pentaware.doorish.BaseFragment;
import com.pentaware.doorish.R;

public class PrivacyPolicy extends BaseFragment {

    private PrivacyPolicyViewModel mViewModel;
    private View mView;

    public static PrivacyPolicy newInstance() {
        return new PrivacyPolicy();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.privacy_policy_fragment, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PrivacyPolicyViewModel.class);
        // TODO: Use the ViewModel

        /*Button btnShowPolicy = (Button)mView.findViewById(R.id.btnShowPolicy);
        btnShowPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://essentials-933fb.web.app/privacy_policy.html";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);

            }
        });*/


    }
//    private String getBody(){
//
//      String sBody = "<h3>Privacy Policy - My Rupeaze</h3>"
//              + "<div><p>"
//               + "THIS PRIVACY POLICY IS AN ELECTRONIC RECORD IN THE FORM"
//               + "OF AN ELECTRONIC CONTRACT FORMED UNDER THE INFORMATION TECHNOLOGY ACT, 2000 AND THE RULES MADE THEREUNDER AND THE AMENDED PROVISIONS PERTAINING TO ELECTRONIC DOCUMENTS/ RECORDS IN VARIOUS STATUTES AS"
//              + "AMENDED BY THE INFORMATION TECHNOLOGY ACT. 2000. THIS PRIVACY POLICY DOES NOT REQUIRE ANY PHYSICAL, ELECTRONIC OR DIGITAL SIGNATURE.</p></div>"
//              + "<div> <h3>Introduction</h3>"
//              + " <p> Expediscia Telecome Private Limited  (hereinafter 'My Rupeaze') recognizes the importance of privacy of its users and also of maintaining confidentiality of the information provided by its users as a responsible data controller and data processer.</p>"
//              + "<p>"
//              +  "This Privacy Policy provides for the practices for handling and securing user's Personal Information (defined hereunder) by My Rupeaze and its subsidiaries and affiliates.</p>"
//              + "<p>"
//              + " This Privacy Policy is applicable to any person ('User') who uses My Rupeaze's customer interface channels including its website, mobile site & mobile app including call centres and offices (collectively"
//              +  " referred herein as ' Website') and agree to be bound  by the terms and conditions of this Privacy Policy. You expressly consent to our use and disclosure of personal information provided by you in accordance with this Privacy Policy. If you disagree with this Privacy Policy please do not use or access our Website This Privacy Policy is incorporated into and subject to the Terms of Use."
//              + "</p>"
//              + "<p>"
//               + "for the purpose of this Privacy Policy, wherever the context so requires ' you/ You' or ' your/ Your' shall mean User and the term ' We', ' us/ Us', ' our/ Our' shall mean My Rupeaze. For the purpose of this Privacy Policy, Website means My Rupeaze's website (s), mobile site (s) and mobile app (s). This Privacy Policy does not apply to any website (s). mobile sites and mobile apps of third parties, even if their websites/ products are linked to our Website. User should take note that information and privacy practices of My Rupeaze's business partners, advertisers, sponsors or other sites to which My Rupeaze provides hyperlink (s) .may be materially different from this Privacy Policy. Accordingly. it is recommended that you review the privacy statements and policies of any such third parties with whom they interact. </p>"
//              + "<p>"
//               + "This Privacy Policy is an integral part of your Terms of Use with My Rupeaze and all capitalized terms used, but not otherwise defined herein, shall have the respective meanings as ascribed to them in the Terms of Use. </p>"
//              + " <p> By using the Website and/ or by providing your information, You consent to the collection and use of the information you disclose on the Website in accordance with this Privacy Policy, including but not limited to Your consent for sharing your information as per this privacy policy. If you disclose any personal information relating to other people to us, you represent that you have the authority to do so and to permit us to use the information in accordance with this Privacy Policy. </p> </div>"
//             +  "<h3>Type of Information we collect and It's Legal Basis</h3> <p> The information as detailed below is collected for us to be able to provide the services chosen by you and also to fulfil our legal obligations as well as our obligations towards third parties as per our Terms of Use </p>"
//              + "<p> Personal Information of User shall include the information shared by the User and collected by us for the following purposes Registration on the Website: Information which you provide while subscribing to or registering on the Website, including but not limited to information about your personal identity such as name, gender, marital status, religion, age etc., your contact details such as your email address, postal addresses, telephone (mobile or otherwise) and/ or fax numbers and any other information relating to your income and/ or lifestyle: billing information payment history etc. (as shared by you). Other information: We many also collect some other information and documents including but not limited to: <ul> <li>Transactional history (other than banking details) about your e-commerce activities, buying behaviour. </li> <li> Your usernames, passwords, email addresses and other security-related information used by you in relation to our Services. </li> <li> Data either created by you or by a third party and which you wish to store on our servers such as image files, documents etc. </li> <li> Data available in public domain or received from any third party including social media channels. including but not limited to personal or non personal information from your linked social media channels (like name, email address, friend list, profile pictures or any other information that is permitted to be received as per your account settings) as a part of your account information. </li> <li> Information pertaining to any other person for who you make a purchase through your registered My Rupeaze account. In such case, you must confirm and represent that each of the other person for whom a purchase has been made, has agreed to have the information shared by you disclosed to us and further be shared by us with the concerned service provider (s). </li> </ul></p>"
//              + "<h3>USE OF DEMOGRAPHIC/ PROFILE DATA/ YOUR INFROMATION </h3> <p> The Personal Information collected may be used in the following manner: While making a purchase While making a purchase, we may use Personal Information including, payment details which include cardholder name, credit/ debit card number (in encrypted form) with expiration date, banking details, wallet details etc. as shared and allowed to be stored by you. We may also use your Personal Information for several reasons including but not limited to: <ul> <li> Confirm your orders with the sellers. </li> <li> Keep you informed of the transaction status. </li> <li> Send order confirmations either via sms or Whatsapp or any other messaging service. </li> <li> Send any updates or changes to your order (s); allow our customer service to contact you, if necessary </li> <li> Customize the content of our Website, mobile site and mobile app. </li> <li> Request for reviews of products or services or any other improvements. </li> <li> Send verification message (s) or email (s) </li> <li> Validate/ authenticate your account and to prevent any misuse or abuse. </li> <li> Contact you on your birthday/ anniversary to offer a special gift or offer.We use personal information to provide the services you request. To the extent we use your personal information to market to you, we will provide you the ability to opt-out of such uses. We use your personal information to assist sellers in handling and fulfilling orders, enhancing customer experience, resolve disputes troubleshoot problemsi help promote a safe service collect money, measure consumer interest in our products or services (if applicable).inform you about online and offline offers, products, and updatesicustomize and enhance your experience detect and protect us against error. fraud and other criminal activity: enforce our terms and conditions:and as otherwise described to you at the time of collection.</li> <li> In our efforts to continually improve our product offerings, we collect and analyse demographic and profile data about our users activity on our Website.</li> <li> We identify and use your IP address to help diagnose problems with our server, and to administer our Website. Your IP address is also used to help identify you and to gather broad demographic information.</li> <li> We will occasionally ask you to complete optional online surveys. These surveys may ask you for personal information, contact information, date of birth, demographic information (like zip code, age, or income level), attributes such as your interests, household or lifestyle information. your purchasing behaviour or history, preferences, and other such information that you may choose to provide. We use this data to tailor your experience at our Website, providing you with content that we think you might be interested in and to display content according to your preferences.</li></ul></p>"
//
//    <h4>Cookies</h4>
//    <p>
//                My Rupeaze uses cookies to personalize your
//        experience on the Website and the
//        advertisements that maybe displayed. My Rupeaze's use of cookies is similar to that of
//        any other reputable online companies. Cookies
//        are small pieces of information that are
//        stored by your browser on your device's hard
//        drive. Cookies allow us to serve you better and
//        more efficiently.
//    </p>
//
//    <p>
//                Cookies also allow ease of
//        access, by logging you in without having to type
//        your login name each time (only your password
//                is needed); we may also use such cookies to
//        display any advertisement (s) to you while you
//        are on the Website or to send you offers (or
//                similar emails-provided you have not opted
//                out of receiving such emails) focusing on
//        destinations which may be of your interest.
//                Most web browsers automatically accept
//        cookies. Of course, by changing the options on
//        your web browser or using certain software
//        programs, you can control how and whether
//        cookies will be accepted by your browser.My Rupeaze supports your right to block any
//        unwanted Internet activity, especially that of
//        unscrupulous websites. However, blocking My Rupeaze cookies may disable certain features
//        on the Website, and may hinder an otherwise
//        seamless experience to purchase or use
//        certain services available on the
//        Website.Please note that it is possible to block
//        cookie activity from certain websites while
//        permitting cookies from websites you trust.
//    </p>
//
//
//    <h4>Automatic Logging of Session Data</h4>
//    <p>
//                Each time you access the Website your session
//        data gets logged. Session data may consist of
//        various aspects like the IP address, operating
//        system and type of browser software being
//        used and the activities conducted by the User
//        while on the Website. We collect session data
//        because it helps us analyse User's choices,
//        browsing pattern including the frequency of
//        visits and duration for which a User is logged on.
//        It also helps us diagnose problems with our
//        servers andlets us better administer our
//        systems. The aforesaid information cannot
//        identify any User personally. However, it may be
//        possible to determine a User's Internet Service
//        Provider (ISP), and the approximate geographic
//        location of User's point of connectivity
//        through the above session data.
//                </p>
//
//    <h4>WITH WHOM YOUR PERSONAL INFORMATION IS SHARED</h4>
//    <p>
//                In the interests of improving personalization and
//        service efficiency, we may, under controlled and
//        secure circumstances, share your Personal
//        Information with our affiliate or associate entities.
//                This will enable us to provide you with information
//        about various products which might interest you or
//        help us address your questions and requests in
//        relation to your bookings.
//        If the assets of My Rupeaze are acquired, our
//        customer information may also be transferred to
//        the acquirer depending upon the nature of such
//        acquisition. In addition, as part of business
//        expansion/ development/ restructuring or for any
//        other reason whatsoever, if we decide to
//        sell/ transfer/ assign our business, any part thereof.
//        any of our subsidiaries or any business units, then as
//        part of such restructuring exercise customer
//        information including the Personal Information
//        collected herein shall be transferred accordingly.
//        Business Partners and Third-Party Vendors
//        You hereby unconditionally agree and permit that
//        My Rupeaze may transfer, share, disclose or part
//        with all or any of Your Information, within and outside
//        of the Republic of India to various My Rupeaze
//        entities and to third party service providers/
//                partners/ banks and financial institutions for one or
//        more of the Purposes or as may be required by
//        applicable law. In such case we will contractually
//        oblige the receiving parties of the Information to
//        ensure the same level of data protection that is
//        adhered to by My Rupeaze under applicable law.
//        You acknowledge and agree that, to the extent
//        permissible under applicable laws, it is adequate that
//        when My Rupeaze transfers Your Information to any
//        other entity within or outside the country of
//        residence, My Rupeaze will place contractual
//        obligations on the transferee which will oblige the
//        transferee to adhere to the provisions of this
//        Privacy Policy.
//    </p>
//
//    <p>
//                You unconditionally agree and permit My Rupeaze to
//        access and read your SMS to autofill or prepopulate
//        OTP ' while making a transaction and to validate your
//        mobile number as may be applicable.
//        My Rupeaze may share statistical data and other
//        details (other than Your Personal Information)
//        without your express or implied consent to
//        facilitate various programmes or initiatives
//        launched by My Rupeaze, its affiliates, agents, third
//        party service providers. partners or banks &
//                financial institutions, from time to time.
//                We may transfer/ disclose/ share Information (other
//                than Your Personal Information) to those parties who
//        support our business, such as providing technical
//        infrastructure services, analysing how our services
//        are used, measuring the effectiveness of
//        advertisements, providing customer/ buyer
//        services, facilitating payments, or conducting
//        academic research and surveys. These affiliates and
//        third-party service providers shall adhere to
//        confidentiality obligations consistent with this
//        Privacy Policy. Notwithstanding the above, we use
//        other third parties such as a credit/debit card
//        processing company, payment. gateway. pre-paid
//        cards etc. to enable You to make payments for buying
//        products or availing services on My Rupeaze. When
//        You sign up for these services. You may have the
//        ability to save Your card details and/or UPI details for
//        futurereference and faster future payment.s. In
//        such case, We may share Your relevant Personal
//        Information as necessary for the third parties t.o
//        provide such services. including your name, residence
//        and email address. The processing of payments or
//        authorization is solely in accordance with these third
//        parties policies, terms and conditions and we are not
//        in any manner responsible or liable to You or any third
//        party for any delay or failure at their end in
//        processing the payments.
//                </p>
//
//    <p>
//                My Rupeaze may also share Personal Information if
//        My Rupeaze believe it is necessary in order to
//        investigate, prevent, or take action regarding illegal
//        activities, suspecled fraud. situations involving
//        potential threats to the physical safety of any
//        person, violat.ions of various terms and conditions or
//        our policies.
//    </p>
//
//    <div>
//      <h3 class="h1Terms">DISCLSOURE OF INFORMATION</h3>
//      <p>
//                In addition to the circumstances described above.
//        My Rupeaze may disclose User's Personal
//        Information if required to do so:
//
//      <ul>
//        <li> By law.required by any enforcement authority
//        for investigation, by court order or in
//        reference to any legal process:
//        to conduct our business:
//        </li>
//
//        <li>
//                For regulatory, internal compliance and audit
//        ехегcise(s)
//                </li>
//
//        <li>
//                To secure our systems or
//        to enforce or protect our rights or properties
//        of Club factory or any or all of its affiliates.
//        associates, employees, directors or officers or
//        when we have reason to believe that disclosing
//        Personal Information of User(s) is necessary
//        to identify.cont.act or bring legal action
//        against someone who may be causing
//        interference with our rights or properties.
//        whether intentionally or otherwise. or when
//        anyone else could be harmed by such activities.
//        Such disclosure and storage may take place without
//        your knowledge. In that case, we shall not be liable to
//        you or any third party for any damages howsoever
//        arising from such disclosure and storage.
//        </li>
//      </ul>
//      </p>
//    </div>
//
//    <div>
//      <h3 class="h1Terms">HOW CAN YOU OPT-OUT OF RECEIVING QUR PROMOTIONAL E-MAILS?</h3>
//      <p>
//                You will occasionally receive e-mail updates from us
//        about order information and other noteworthy
//        items. We hope you will find these updates
//        interesting and informative. If you wish not to
//        receive them, please click on the unsubscribe" link or
//        follow the instructions in each e-mail message.
//                </p>
//
//    </div>
//
//    <div>
//      <h3 class="h1Terms">HOW WE PROTECT YOUR PERSONAL INFORMATION</h3>
//      <p>
//                The seller for any help reagarding the process or any such help can send communications to help@myrupeaze.com
//      </p>
//
//    </div>
//
//    <div>
//      <h3 class="h1Terms">Account Termination and Dispute's Settlement</h3>
//                <p>
//                All payments on the Website are secured. This means
//        all Personal Information you provide is transmitted
//        using TLS (Transport layer Security) encryption. TSL
//        is a proven coding system that lets your browser
//        automatically encrypt.or scramble, data before you
//        sendit to us. Website has stringent security
//        measures in place to protect the loss.misuse, and
//        alteration of the information under our control.
//                </p>
//
//      <p>
//                Whenever you change or access your account
//        information. we offer the use of a secure server.
//        Once your information is in our possession. we adhere
//        to strict security guidelines. protecting it against
//        unauthorized access.
//      </p>
//
//      <p>
//                It is important for you to protect against
//        unauthorised access to your password and to your
//        computer. Be sure to sign off when you finish using a
//        shared comnuter
//      </p>
//
//    </div>
//
//    <div>
//      <h3 class="h1Terms">THIRD PARTY LINKS</h3>
//      <p>
//                Our Websitelinks Lo other websites that may collect
//        personally identifiable information about you.
//                MyRupeaze.com is not responsible for the privacy
//        practices or the content of those linked websites.
//      </p>
//
//    </div>
//
//    <div>
//      <h3 class="h1Terms">WITHDRAWAL OF CONSENT AND PERMISSION</h3>
//      <p>
//                You may withdraw your consent to submit any or all
//        Personal Information or decline to provide any
//        permissions on as covered above at any time. In case.
//        you choose to do so then your access to the Website
//        may be limited, or we might not be able to provide the
//        services to you. You may withdraw your consent by
//        sending an email to feedback@myrupeaze.com
//      </p>
//
//    </div>
//
//    <div>
//      <h3 class="h1Terms">YOUR RIGHT QUA PERSONAL INFORMATION</h3>
//      <p>
//                You may access your Personal Information from your
//        User account with My Rupeaze. You may also correct
//        your personal information or delete such information
//                (except some mandatory fields) from your User
//        account directly. If you don't have such a User
//        account, then you write to
//        feedback@myrupeaze.com
//
//      </p>
//
//    </div>
//
//    <div>
//      <h3 class="h1Terms">ELIGIBILITY TO TRANSACT WITH My Rupeaze</h3>
//      <p>
//                You must at least 18 years of age to transact
//        directly with My Rupeaze and also to consent to the
//        processing of your personal data
//                </p>
//
//    </div>
//
//    <div>
//      <h3 class="h1Terms">HOW LONG DO WE KEEP YOUR PERSONAL INFORMATION</h3>
//      <p>
//                My Rupeaze will retain your Personal Information on
//        its servers for as long as is reasonably necessary for
//        the purposes listedin this policy. In some
//        circumstances we may retain your Personal
//        Information for longer periods of time. for instance
//        where we are required Lo do so in accordance with any
//        legal, regulatory, Lax or accounting requirements.
//                </p>
//
//      <p>
//                Where your personal data is no longer required. we
//        will ensure it is either securely deleted or stored in a
//        way which means it will no longer be used by the
//        business.
//                </p>
//
//    </div>
//
//    <div>
//      <h3 class="h1Terms">CHANGES TO My Rupeaze</h3>
//      <p>
//                We reserve the rights torevise the Privacy Policy
//        from time to time to suit various legal. business and
//        customer requirement. You are required to be aware
//        of any changes and review this policy periodically.
//      </p>
//
//    </div>
//
//
//    <div>
//      <h3 class="h1Terms">Grievance Addressing</h3>
//      <p>
//                In accordance with Information Technology Act. 2000
//        and rules made thereunder. the name and contact
//        details of the Grievance Officer is specified below:
//      </p>
//
//      <p>
//                Name: Grievance Officer <br />
//                Email: grievance@myrupeaze.com <br />
//                Registered Address-Kalindipuram,Rajrooppur, Prayagraj, Uttar Pradesh
//                </p>
//
//      <p>
//                You may always submit concerns regarding this
//        Privacy Policy via email to us at
//        grievance@myrupeaze.com. My Rupeaze shall
//        endeavour to respond to all reasonable concerns and
//        inquires
//                </p>
//
//    </div>
//    }


}

