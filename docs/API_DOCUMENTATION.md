# Smart CRM & Lead Management System — API Specification

The Smart CRM application provides a secure RESTful API built on Spring Boot 3.3.4, secured by Spring Security and JWT.

Interactive OpenAPI 3.0 documentation is available at `/swagger-ui.html` and raw JSON at `/v3/api-docs`.

---

## 1. Authentication Endpoints

### Register User
- **Method:** `POST`
- **URL:** `/api/auth/register`
- **Access:** Public
- **Request Body:**
```json
{
  "fullName": "Jane Doe",
  "email": "jane@company.com",
  "password": "SecurePassword123!",
  "role": "SALES_REP"
}
```
- **Response (200 OK):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOi...",
    "tokenType": "Bearer",
    "user": {
      "id": 2,
      "email": "jane@company.com",
      "fullName": "Jane Doe",
      "role": "SALES_REP"
    }
  }
}
```

### User Login
- **Method:** `POST`
- **URL:** `/api/auth/login`
- **Access:** Public
- **Request Body:**
```json
{
  "email": "admin@smartcrm.com",
  "password": "Admin@123CRM!"
}
```

### Current User Profile
- **Method:** `GET`
- **URL:** `/api/auth/me`
- **Access:** Authenticated (`Bearer <token>`)

---

## 2. Lead Management Endpoints

### List Leads (with Pagination, Search, Status & Priority filters)
- **Method:** `GET`
- **URL:** `/api/leads?page=0&size=10&search=Wayne&status=QUALIFIED&priority=HIGH`
- **Access:** Authenticated

### Create Lead
- **Method:** `POST`
- **URL:** `/api/leads`
- **Access:** Authenticated

### Get Lead Details
- **Method:** `GET`
- **URL:** `/api/leads/{id}`
- **Access:** Authenticated

### Update Lead
- **Method:** `PUT`
- **URL:** `/api/leads/{id}`
- **Access:** Authenticated

### Update Lead Stage / Status
- **Method:** `PATCH`
- **URL:** `/api/leads/{id}/status`
- **Access:** Authenticated
- **Request Body:**
```json
{
  "status": "PROPOSAL",
  "notes": "Moving to proposal after formal demo"
}
```

### Delete Lead
- **Method:** `DELETE`
- **URL:** `/api/leads/{id}`
- **Access:** Authenticated

---

## 3. Activities & Follow-ups

### Get Lead Activities
- **Method:** `GET`
- **URL:** `/api/leads/{id}/activities`

### Log Activity
- **Method:** `POST`
- **URL:** `/api/leads/{id}/activities`
- **Body:** `type` (`CALL`, `EMAIL`, `MEETING`, `NOTE`, `OTHER`), `title`, `description`, `activityTime`

### Get Follow-ups
- **Method:** `GET`
- **URL:** `/api/follow-ups`

### Schedule Follow-up
- **Method:** `POST`
- **URL:** `/api/follow-ups`

### Complete Follow-up
- **Method:** `PATCH`
- **URL:** `/api/follow-ups/{id}/complete`

---

## 4. AI-Powered Features

### AI Lead Scoring
- **Method:** `POST`
- **URL:** `/api/ai/lead-score`
- **Body:** `{"leadId": 1}`
- **Returns:** Numerical score (0-100), explanation, recommended next action.

### AI Follow-up Message Generator
- **Method:** `POST`
- **URL:** `/api/ai/follow-up-message`
- **Body:** `{"leadId": 1, "channel": "EMAIL", "tone": "PROFESSIONAL", "purpose": "Meeting follow up"}`
- **Returns:** AI-drafted subject line and message body.

### AI Lead Summary
- **Method:** `POST`
- **URL:** `/api/ai/lead-summary`
- **Body:** `{"leadId": 1}`

### AI Sales Assistant Chatbot
- **Method:** `POST`
- **URL:** `/api/ai/chat`
- **Body:** `{"message": "Which leads need follow up today?"}`
