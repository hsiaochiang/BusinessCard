Project Technology & Architecture Constraints for Planning

1. Mobile Platform
- The mobile app will be implemented using Android (Kotlin).
- App must support both camera capture and manual editing of card data.
- Offline-first experience is required. The app should store pending changes locally when offline and sync when network becomes available.

2. OCR Engine
- Primary OCR: Google ML Kit on-device text recognition.
- No cloud-based OCR (e.g., Google Cloud Vision API) in Phase 1.
- The plan should include guidance for field mapping (name, company, phone, email, address) for Chinese business cards.

3. Data Storage
- Google Sheets will serve as the single source of truth.
- Data synchronization must use Google Sheets API v4.
- No backend server in Phase 1.
- No Redis, no external SQL/NoSQL database.
- Optional: local cache using SQLite only if needed for offline conflict handling.

4. Deletion Strategy
- Adopt soft delete using an `is_deleted` flag with timestamp and deleted_by columns.
- The system must avoid hard deletes in Phase 1.

5. Authentication & Permissions
- No user authentication system for Phase 1.
- The app will use built-in Google OAuth for accessing the Sheets API.
- No multi-user permission system at this stage.

6. File & Character Requirements
- All data must support UTF-8 and handle Traditional Chinese text without loss.
- OCR accuracy considerations for Chinese must be reflected in risks and planning.

7. Required User Workflows
- Capture card using camera.
- Apply ML Kit OCR.
- Parse text into structured fields.
- Allow user to review and edit fields.
- Save/append/update to Google Sheets.
- Optional: export single card or all cards as CSV inside the app.

8. Deliverables Required in the Plan
- Work breakdown structure (WBS) for Phase 1.
- System architecture diagram.
- Data flow for mobile → Sheets sync.
- Risk list with mitigation strategies, focusing on:
  - OCR accuracy for Chinese text
  - Google Sheets API quota limits
  - Offline sync conflicts
  - Multi-device data consistency
  - Device performance differences

9. Additional Notes
- No frontend Web UI is required.
- No backend services should be assumed in Phase 1.
- Future backend extensions may be suggested but clearly separated from Phase 1 deliverables.
