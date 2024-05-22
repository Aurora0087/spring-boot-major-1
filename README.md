### Build Instructions

To build and install the project, follow these steps:

1. **Clone the Repository** (if you haven't already):
   ```bash
   git clone https://github.com/Aurora0087/spring-boot-major-1.git
   cd <repository_directory>
   ```

2. **Install Dependencies and Build the Project:**
   Use Maven Wrapper (`./mvnw`) to clean, compile, and package the project:
   ```bash
   ./mvnw clean install
   ```

This will ensure that the project is built successfully and the necessary dependencies are downloaded and installed. After running the above command, you should see the build success message indicating that the project is ready for use.

---

To set up the `application.properties` file for your application, follow these steps:

1. Navigate to the `src/main/resources/` directory in your project.

2. Locate the `example.application.properties` file. This file serves as a template for your actual `application.properties` file.

3. Rename `example.application.properties` to `application.properties`.

4. Create a new file named `application.properties` in the `src/main/resources/` directory if it doesn't exist already.

5. Open the `application.properties` file in a text editor.

6. Set up the values of the properties according to your application's requirements. These properties typically include configuration settings such as database connection details, server port, logging levels, etc.

Here's an example of how your `application.properties` file might look:

```properties
#                            Db related
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://localhost:3306/mydatabase
spring.datasource.username= myusername
spring.datasource.password= mypassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.id.new_generator_mappings=true
server.error.include-message=always
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true

#                             Amazon s3
amazon.s3.endpointUrl = https://example.amazonaws.com
amazon.s3.bucketName = exampleBucketName
amazon.s3.accessKey = *********
amazon.s3.secretKey = *********


#                           File request and resp[one size
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB


#                           RazorPay
razorpay.key.id = **
razorpay.key.secret = **
razorpay.webhook.secret = **
```

Make sure to replace the placeholder values (`mydatabase`, `myusername`, `mypassword`,`exampleBucketName` etc.) with your actual database connection details and other configurations.

Once you've set up the `application.properties` file, you can save it. This file will be used by your application to load the specified configurations during runtime.

---

# API Endpoints

This is a Spring Boot application that provides various API endpoints for user authentication, video content management, and category management.

## Table of Contents

- [Endpoints](#endpoints)
  - [User Authentication](#user-authentication)
    - [Register User](#register-user)
    - [Login](#login)
    - [Logout](#logout)
    - [Authenticate](#authenticate)
  - [Video Content Management](#video-content-management)
    - [Upload Video](#upload-video)
    - [Update Video Content](#update-video-content)
    - [Delete Video Content](#delete-video-content)
    - [Get All Video Contents](#get-all-video-contents)
    - [Get Video Content by ID](#get-video-content-by-id)
    - [Watch Video](#watch-video)
    - [Like Video](#video-likes)
    - [Search Videos by Category](#search-videos-by-category)
    - [Search Videos by Topic](#search-videos-by-topic)
  - [Category Management](#category-management)
    - [Add Category](#add-category)
    - [Get Categories](#get-categories)
  - [Comment Management](#comment-management)
    - [Add Comment](#add-comment)
    - [Edit Comment](#edit-comment)
    - [Like Comment](#like-comment)
    - [Get Comment by ID](#get-comment)
    - [Get Child Comments](#get-child-comments)
    - [Make Comment Private](#make-comment-private)
    - [Make Comment Public](#make-comment-public)
  - [Order Management](#order-management)
    - [Create Order](#create-order)
    - [Order Success](#order-success)
  - [Profile Management](#profile)
    - [Get Profile](#get-profile)
    - [Edit Profile](#edit-profile)
    - [Edit Profile Images](#edit-profile-images)
    - [Edit Profile Password](#edit-profile-password)
  - [Admin Management](#admin-management)
    - [Get Users](#get-users)
    - [Update User Permissions](#update-user-permissions)
  - [Cart Management](#cart-management)
    - [Add to Cart](#add-to-cart)
    - [Get Cart Items](#get-cart-items)
    - [Delete Cart Item](#delete-cart-item)
    - [Order from Cart](#order-from-cart)
- [Webhook](#webhook)
  - [Razorpay After payment](#razorpay-after-payment)

## Endpoints

### User Authentication

#### Register User

- **URL**: `/register`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "firstName": "fn",
    "lastName": "ln",
    "email": "demo@email.com",
    "password": "456",
    "confirmPassword": "456"
  }
  ```
- **Response**:
  ```json
  {
    "message": "User registration done."
  }
  ```

#### Login

- **URL**: `/login`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "email": "demo@email.com",
    "password": "456"
  }
  ```
- **Response**:
  ```json
  {
    "message": "success"
  }
  ```

#### Logout

- **URL**: `/logout`
- **Method**: `GET`
- **Request Body**: None
- **Response**:
  ```json
  {
    "message": "Log-out."
  }
  ```

#### Authenticate

- **URL**: `/auth`
- **Method**: `GET`
- **Request Body**: None (uses cookies)
- **Response**:
  ```json
  {
    "avatar": "https://example.amazoneaws.com/avatar/defImage.jpg",
    "uid": "1000"
  }
  ```

### Video Content Management

#### Upload Video

- **URL**: `/upload`
- **Method**: `POST`
- **Request Parameters**:
  - `thumbnail`: Image File (any image format)
  - `video`: Video File (any video format)
  - `title`: Text
  - `categoryID`: Text (Category id number from any existing category)
  - `description`: Text
  - `price`: Text (Price value number)
- **Response**:
  ```json
  {
    "message": "Done",
    "contentId": 1001
  }
  ```

#### Update Video Content

- **URL**: `/update/videocontent/{vcid}`
- **Method**: `PUT`
- **Request Path Variable**: `vcid` (Video Content ID)

#### Delete Video Content

- **URL**: `/delete/video/{vcid}`
- **Method**: `DELETE`
- **Request Path Variable**: `vcid` (Video Content ID)

#### Get All Video Contents

- **URL**: `/get/videocontents`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "limit": "5",
    "ignore": "0"
  }
  ```
- **Response**:
  ```json
  {
    "message": "Done",
    "contents": [
      {
        "author": {
          "profileId": 1000,
          "userName": "debrajbanshi1@gmail.com",
          "firstName": "deb",
          "lastName": "r",
          "avatarUrl": "https://example.amazoneaws.com/avatar/defImage.jpg"
        },
        "vid": 1000,
        "title": "Demo Content",
        "description": "Video description here...",
        "price": 53.1,
        "category": "music",
        "thumbnailUrl": "https://example.amazoneaws.com/images/09499d40-478f-4019-8847-298c5a7910c3pexels-wendy-wei-1864637.jpg",
        "createAt": "2024-05-17T06:51:34.825+00:00",
        "likeList": [1002]
      }
    ],
    "isMore": false
  }
  ```

#### Get Video Content by ID

- **URL**: `/get/videocontent/{vcid}`
- **Method**: `GET`
- **Request Path Variable**: `vcid` (Video Content ID)
- **Response**:
  ```json
  {
    "videoContent": {
      "author": {
        "profileId": 1002,
        "userName": "demo@mail.com",
        "firstName": "demo",
        "lastName": "user",
        "avatarUrl": "https://example.amazoneaws.com/avatar/defImage.jpg"
      },
      "vid": 1001,
      "title": "delete this [edited]",
      "description": "fgbfgbnbfnbn bfgbb fbfg fbhbt bhrb bfgbnn bfb tbtfr bbhtbv rtbhrbh btbrb [edited]",
      "price": 81.0,
      "category": "music",
      "thumbnailUrl": "https://example.amazoneaws.com/images/bbbac8c5-d967-4df4-99e9-078bce1a838d1.jpg",
      "createAt": "2024-05-17T06:52:46.606+00:00",
      "likeList": []
    },
    "message": "Done"
  }
  ```

#### Watch Video

- **URL**: `/watch/{vcid}`
- **Method**: `POST`
- **Request Path Variable**: `vcid` (Video Content ID)
- **Response**:
  ```json
  {
    "message": "Done",
    "videoUrl": "https://example.amazoneaws.com/videos/d02f1aa9-a2a6-4312-bd8b-7ecefd52f724pexels-tom-fisk-20317587.mp4"
  }
  ```

#### Video Likes
#### /like/video/{vcid}
**Method:** POST  
**Request Path Variable:**  
Example: `localhost:8080/like/video/1001`

**Response Example:**
```json
{
    "message": "Done",
    "likeList": [
        1002
    ]
}
```

#### Search Videos by Category

- **URL**: `/search/byCategory/{category}`
- **Method**: `POST`
- **Request Path Variable**: `category` (Category name)
- **Request Body**:
  ```json
  {
    "limit": "5",
    "ignore": "0"
  }
  ```
- **Response**:
  ```json
  {
    "message": "Done",
    "contents": [
      {
        "author": {
          "profileId": 1000,
          "userName": "debrajbanshi1@gmail.com",
          "firstName": "deb",
          "lastName": "r",
          "avatarUrl": "https://example.amazoneaws.com/avatar/defImage.jpg"
        },
        "vid": 1000,
        "title": "Demo Content",
        "description": "Video description here...",
        "price": 53.1,
        "category": "music",
        "thumbnailUrl": "https://example.amazoneaws.com/images/09499d40-478f-4019-8847-298c5a7910c3pexels-wendy-wei-1864637.jpg",
        "createAt": "2024-05-17T06:51:34.825+00:00",
        "likeList": [1002]
      }
    ],
    "isMore": false
  }
  ```

#### Search Videos by Topic

- **URL**: `/search/{topic}`
- **Method**: `POST`
- **Request Path Variable**: `topic` (Topic name)
- **Request Body**:
  ```json
  {
    "limit": "5",
    "ignore": "0"
  }
  ```
- **Response**:

  ```json
  {
    "message": "Done",
    "contents": [
      {
        "author": {
          "profileId": 1002,
          "userName": "demo@mail.com",
          "firstName": "demo",
          "lastName": "user",
          "avatarUrl": "https://example.amazoneaws.com/avatar/defImage.jpg"
        },
        "vid": 1001,
        "title": "delete this [edited]",
        "description": "fgbfgbnbfnbn bfgbb fbfg fbhbt bhrb bfgbnn bfb tbtfr bbhtbv rtbhrbh btbrb [edited]",
        "price": 81.0,
        "category": "music",
        "thumbnailUrl": "https://example.amazonaws.com/images/bbbac8c5-d967-4df4-99e9-078bce1a838d1.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240517T080949Z&X-Amz-SignedHeaders=host&X-Amz-Expires=21600&X-Amz-Credential=AKIAR6AG2W6AQMUAG72N%2F20240517%2Fap-south-1%2Fs3%2Faws4_request&X-Amz-Signature=23bce436d207f0cdd214612dd31ac145ae57bcaadfe3c70811ded58e6cf769bb",
        "createAt": "2024-05-17T06:52:46.606+00:00",
        "likeList": []
      }
    ],
    "isMore": false
  }
  ```

### Category Management

#### Add Category
#### /add/category
**Method:** POST  
**Request Parameters:**

| Key      | Value Type |
|----------|------------|
| Category | Text       |

**Response Example:**
```json
"New Category 'test category 1' added."
```

#### Get Categories
#### /get/category
**Method:** GET  
**Request Body:**
None

**Response Example:**
```json
[
    {
        "id": 1,
        "categoryName": "music"
    },
    {
        "id": 2,
        "categoryName": "animation"
    },
    {
        "id": 3,
        "categoryName": "test category 1"
    }
]
```

### Comment Management

#### Add Comment
#### /add/comment
**Method:** POST  
**Request Body Examples:**

1. Adding a comment to a video:
```json
{
    "parentId": "1001",
    "text": "ssssssssssssfffff",
    "parentType": "video"
}
```
**Response Example:**
```json
100
```

2. Adding a comment to another comment:
```json
{
    "parentId": "100",
    "text": "ssssssssssssfffff",
    "parentType": "comment"
}
```
**Response Example:**
```json
1001
```

#### Edit Comment
#### /edit/comment
**Method:** POST  
**Request Body:**
```json
{
    "commentId": "100",
    "newText": "edited comment"
}
```
**Response Body:**
```json
"Edit's done."
```

#### Like Comment
#### /like/comment/{commentId}
**Method:** POST  
**Request Path Variable:**  
Example: `localhost:8080/like/comment/100`

**Response Example:**
```json
{
    "message": "Done",
    "likeList": [
        1002
    ]
}
```

#### Get Comment
#### /get/comment/{commentId}
**Method:** GET  
**Request Path Variable:**  
Example: `localhost:8080/get/comment/100`

**Response Example:**
```json
{
    "isPrivate": false,
    "message": null,
    "commentId": 100,
    "text": "edited comment",
    "author": {
        "profileId": 1002,
        "userName": "demo@mail.com",
        "firstName": "demo",
        "lastName": "user",
        "avatarUrl": "https://example.amazonaws.com/avatar/defImage.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240517T092322Z&X-Amz-SignedHeaders=host&X-Amz-Expires=21599&X-Amz-Credential=AKIAR6AG2W6AQMUAG72N%2F20240517%2Fap-south-1%2Fs3%2Faws4_request&X-Amz-Signature=092b2974e7a4414f2cbeee9f7db3c68dc0d3b80ffde7ff0b8302267d8e0a336f"
    }
}
```

#### Get Child Comments
#### /get/childComment
**Method:** POST  
**Request Body Examples:**

1. For video parent:
```json
{
    "parentId": "1001",
    "parentType": "video",
    "ignore": "0",
    "limit": "2"
}
```
**Response Example:**
```json
{
    "message": "Done.",
    "commentList": [
        {
            "isPrivate": false,
            "message": null,
            "commentId": 100,
            "text": "edited comment",
            "author": {
                "profileId": 1002,
                "userName": "demo@mail.com",
                "firstName": "demo",
                "lastName": "user",
                "avatarUrl": "https://example.amazonaws.com/avatar/defImage.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240517T092322Z&X-Amz-SignedHeaders=host&X-Amz-Expires=21599&X-Amz-Credential=AKIAR6AG2W6AQMUAG72N%2F20240517%2Fap-south-1%2Fs3%2Faws4_request&X-Amz-Signature=092b2974e7a4414f2cbeee9f7db3c68dc0d3b80ffde7ff0b8302267d8e0a336f"
            }
        }
    ],
    "isMore": false
}
```

2. For comment parent:
```json
{
    "parentId": "100",
    "parentType": "comment",
    "ignore": "0",
    "limit": "2"
}
```
**Response Example:**
```json
{
    "message": "Done.",
    "commentList": [
        {
            "isPrivate": false,
            "message": null,
            "commentId": 101,
            "text": "ssssssssssssfffff",
            "author": {
                "profileId": 1002,
                "userName": "demo@mail.com",
                "firstName": "demo",
                "lastName": "user",
                "avatarUrl": "https://example.amazonaws.com/avatar/defImage.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240517T092322Z&X-Amz-SignedHeaders=host&X-Amz-Expires=21599&X-Amz-Credential=AKIAR6AG2W6AQMUAG72N%2F20240517%2Fap-south-1%2Fs3%2Faws4_request&X-Amz-Signature=092b2974e7a4414f2cbeee9f7db3c68dc0d3b80ffde7ff0b8302267d8e0a336f"
            }
        }
    ],
    "isMore": false
}
```

#### Make Comment Private
#### /edit/comment/private
**Method:** POST  
**Request Body:**
```json
{
    "commentIds": [
        "101"
    ]
}
```
**Response Body:**
```json
true
```

#### Make Comment Public
#### /edit/comment/public
**Method:** POST  
**Request Body:**
```json
{
    "commentIds": [
        "101"
    ]
}
```
**Response Body:**
```json
true
```

### Order Management

#### Create Order
#### /order/{vcid}
**Method:** POST  
**Request Path Variable:**  
`localhost:8080/order/{vcid}`  
Example: `localhost:8080/order/1001`

**Response Body:**
```json
{
    "message": "success",
    "status": "created",
    "razorpayOrderId": "order_OBW1TQ9eVHKlvQ",
    "key": "rzp_test_********",
    "currency": "INR",
    "amount": "9300",
    "notes": "delete this",
    "description": null,
    "name": "demo user",
    "email": "demo@mail.com"
}
```

#### Order Success
#### /order/success/{uid}
**Method:** GET  
**Request Path Variable:**  
`localhost:8080/order/success/{uid}`  
Example: `localhost:8080/order/success/1002`

**Response Body:**
```json
[
    {
        "contentId": 1001,
        "createdAt": "2024-05-17T10:00:02.736+00:00",
        "paymentId": "pay_OBWJZzWTrEbFXR",
        "orderId": 10003
    }
]
```

## Profile

#### Get Profile
#### /profile/{uid}
**Method:** POST  
**Request Path Variable:**  
`localhost:8080/profile/{uid}`  
Example: `localhost:8080/profile/1001`

**Response Body:**
1. For User 1001:
    ```json
    {
        "message": "Done",
        "profileId": 1001,
        "userName": "arpan@gmail.com",
        "firstName": "arpan",
        "lastName": "m.",
        "avatarUrl": "https://example.amazonaws.com/avatar/defImage.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240518T063034Z&X-Amz-SignedHeaders=host&X-Amz-Expires=21599&X-Amz-Credential=AKIAR6AG2W6AQMUAG72N%2F20240518%2Fap-south-1%2Fs3%2Faws4_request&X-Amz-Signature=deedf4f1dd7c5f4bba6673e85581785c8df123ef8c652e12cf8d5d130fefc22c",
        "email": null,
        "bgImage": "",
        "bio": "",
        "canChange": false
    }
    ```

2. For User 1002:
    ```json
    {
        "message": "Done",
        "profileId": 1002,
        "userName": "demo@mail.com",
        "firstName": "demo",
        "lastName": "user",
        "avatarUrl": "https://example.amazonaws.com/avatar/defImage.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240518T063208Z&X-Amz-SignedHeaders=host&X-Amz-Expires=21599&X-Amz-Credential=AKIAR6AG2W6AQMUAG72N%2F20240518%2Fap-south-1%2Fs3%2Faws4_request&X-Amz-Signature=b4a973fe0fa2d2e78102b78d51d7f71db40b05b9cc027f1a128b1328019dff2c",
        "email": "demo@mail.com",
        "bgImage": "",
        "bio": "",
        "canChange": true
    }
    ```

#### Edit Profile
#### /profile/edit/{uid}
**Method:** POST  
**Request Path Variable:**  
`localhost:8080/profile/edit/{uid}`  
Example: `localhost:8080/profile/edit/1002`

**Request Body:**
```json
{
    "newFirstName": "test",
    "newLastName": "user 1",
    "newBio": "this bio's updated"
}
```

**Response Body:**
```json
"Done"
```

#### Edit Profile images
#### /profile/edit/images/{uid}
**Method:** POST  
**Request Path Variable:**  
`localhost:8080/profile/edit/images/{uid}`  
Example: `localhost:8080/profile/edit/images/1002`

**Request Body:**
- `bgImage`: Image file
- `avatar`: Image file

**Response Body:**
```json
"Done"
```

#### Edit Profile Password
#### /profile/edit/password
**Method:** POST  
**Request Body:**
```json
{
    "oldPassword": "456",
    "newPassword": "789",
    "confirmNewPassword": "789"
}
```

**Response Body:**
```json
"Done."
```

## Admin Management

#### Get users
#### /admin/getusers
**Method:** POST  
**Request Body:**
```json
{
    "ignore": "0",
    "limit": "5"
}
```

**Response Body:**
```json
{
    "message": "Done",
    "userLists": [
        {
            "id": "1000",
            "userName": "debrajbanshi1@gmail.com",
            "email": "debrajbanshi1@gmail.com",
            "firstName": "deb",
            "lastName": "r",
            "locked": false,
            "enabled": true,
            "role": "ADMIN"
        },
        {
            "id": "1001",
            "userName": "arpan@gmail.com",
            "email": "arpan@gmail.com",
            "firstName": "arpan",
            "lastName": "m.",
            "locked": false,
            "enabled": true,
            "role": "ADMIN"
        },
        {
            "id": "1002",
            "userName": "demo@mail.com",
            "email": "demo@mail.com",
            "firstName": "test",
            "lastName": "user 1",
            "locked": false,
            "enabled": true,
            "role": "USER"
        }
    ],
    "isMore": false
}
```

#### Update User Permissions
#### /admin/update/usersPermissions
**Method:** POST  
**Request Body:**
```json
{
    "userPermissions": [
        {
            "id": "1002",
            "locked": true,
            "enabled": true,
            "role": "USER"
        }
    ]
}
```

**Response Body:**
```json
{
    "message": "Done",
    "userLists": [
        {
            "id": "1000",
            "userName": "debrajbanshi1@gmail.com",
            "email": "debrajbanshi1@gmail.com",
            "firstName": "deb",
            "lastName": "r",
            "locked": false,
            "enabled": true,
            "role": "ADMIN"
        },
        {
            "id": "1001",
            "userName": "arpan@gmail.com",
            "email": "arpan@gmail.com",
            "firstName": "arpan",
            "lastName": "m.",
            "locked": false,
            "enabled": true,
            "role": "ADMIN"
        },
        {
            "id": "1002",
            "userName": "demo@mail.com",
            "email": "demo@mail.com",
            "firstName": "test",
            "lastName": "user 1",
            "locked": true,
            "enabled": true,
            "role": "USER"
        }
    ],
    "isMore": false
}
```

### Cart Management

#### Add to Cart
#### /cart/add/{contentId}
**Method:** POST  
**Request Path Variable:**
```
localhost:8080/cart/add/1000
```

**Response Body:**

1. If the content is already in the cart:
```json
{
    "message": "Content id 1000 already added to user cart."
}
```

2. If the content is added successfully:
```json
{
    "message": "Done"
}
```

#### Get Cart Items
#### /cart/get
**Method:** GET  
**Request Path Variable:**
```
localhost:8080/cart/get
```

**Response Body:**
```json
[
    {
        "itemId": 10003,
        "contentId": 1000,
        "price": "53.1",
        "contentTitle": "Demo Content"
    },
    {
        "itemId": 10004,
        "contentId": 1002,
        "price": "53.1",
        "contentTitle": "Demo Content"
    }
]
```

#### Delete Cart Item
#### /cart/delete/{itemId}
**Method:** DELETE  
**Request Path Variable:**
```
localhost:8080/cart/delete/10004
```

**Response Body:**
```json
{
    "message": "Done"
}
```

#### Order from Cart
#### /cart/order
**Method:** POST  
**Request Path Variable:**
```
localhost:8080/cart/order
```

**Response Body:**
```json
{
    "message": "success",
    "status": "created",
    "razorpayOrderId": "order_ODS3vgV1nB8bIY",
    "key": "rzp_test_q6bnQ5RW84TFSg",
    "currency": "INR",
    "amount": "5310",
    "notes": "Multiple bought:  | 10003",
    "description": null,
    "name": "test user 1",
    "email": "demo@mail.com"
}
```

## Webhook

### Razorpay After payment

- **URL**: `/webhook/paymentSuccess`
- **Method**: `POST`