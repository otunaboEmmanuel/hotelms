# 🏨 Acadia Grand Hotel Management System (HMS)

A full-stack backend system for managing hotel bookings and operations — powered by Spring Boot and enhanced with AI using Retrieval-Augmented Generation (RAG) via OpenAI and pgvector.

---

## 🚀 Features

### ✅ Core Hotel Management Functionality
- User Registration & Authentication with JWT
- Room Inventory Management (Availability, Price, Type)
- Booking Flow:
  - Book one or multiple rooms
  - Admin/Staff approval or denial of bookings
  - Auto-update room availability
- Order Management:
  - RoomOrder & RoomOrderItems architecture
  - Cancel orders (returns room availability)

### 🤖 AI Integration with RAG
- Ask questions like:  
  "What comes with the Deluxe Room?" or  
  "Are meals included?"
- Uses:
  - Spring AI
  - OpenAI (GPT-4)
  - pgvector for storing document embeddings
  - PDF hotel brochure as source knowledge
## 📄 AI RAG Workflow

1. PDF brochure of the hotel is split & embedded
2. VectorStore (pgvector) holds embeddings
3. When a user asks a question, the system:
   - Finds similar chunks from the brochure
   - Feeds them into GPT-4 for grounded answers

---

## 🛠 Setup Instructions

### Prerequisites
- Java 17
- Maven
- Docker + Docker Compose
- OpenAI API Key

### Running the Project

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/hotelms.git
   cd hotelms
