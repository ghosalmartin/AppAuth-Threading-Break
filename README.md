# AppAuth-Threading-Break

The bug seems to be some odd timing issue that the threading negates. To reproduce simply open the
non working activity try to sign in with:

*dimefi2460@combcub.com*
*Test1234.*

Then clear the cookies in chrome and try to login with the working activity and you'll see the difference.

In some cases I had to put the startActivityForResult on the different thread as well
