import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MermaidDiagramComponent } from '../mermaid-diagram/mermaid-diagram.component';

interface QuickDiagram {
  id: string;
  name: string;
  definition: string;
}

@Component({
  selector: 'app-diagram-viewer',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatFormFieldModule,
    MermaidDiagramComponent
  ],
  templateUrl: './diagram-viewer.component.html',
  styleUrls: ['./diagram-viewer.component.scss']
})
export class DiagramViewerComponent implements OnInit {
  @Input() title: string = '';
  @Input() description: string = '';
  @Input() diagramType: 'architecture' | 'flow' | 'sequence' | 'database' = 'architecture';
  @Input() showSelector: boolean = true;
  @Input() showActions: boolean = false;
  @Input() showFullDocsLink: boolean = true;
  @Input() theme: 'default' | 'dark' | 'forest' | 'neutral' = 'default';

  selectedDiagramId: string = '';
  selectedDiagramTitle: string = '';
  currentDiagram: string = '';

  quickDiagrams: QuickDiagram[] = [
    {
      id: 'system-overview',
      name: 'System Overview',
      definition: `graph TB
    subgraph "Frontend"
        A[Angular App<br/>:4200]
    end
    subgraph "Backend"
        B[Spring Boot<br/>:8080]
    end
    subgraph "Database"
        C[(MySQL<br/>:3306)]
    end
    
    A -.->|REST API| B
    B -.->|JPA| C

    classDef frontend fill:#e1f5fe
    classDef backend fill:#f3e5f5
    classDef database fill:#e8f5e8
    
    class A frontend
    class B backend
    class C database`
    },
    {
      id: 'user-flow',
      name: 'User Flow',
      definition: `flowchart TD
    A[🏠 Home] --> B{Logged In?}
    B -->|No| C[🔐 Login]
    B -->|Yes| D[📋 Pet List]
    C --> D
    D --> E[👁️ View Pet]
    E --> F[💝 Adopt Pet]
    F --> G[✅ Success]

    classDef start fill:#e1f5fe
    classDef process fill:#f3e5f5
    classDef success fill:#e8f5e8

    class A,G start
    class C,D,E,F process
    class G success`
    },
    {
      id: 'api-sequence',
      name: 'API Authentication',
      definition: `sequenceDiagram
    participant U as User
    participant F as Frontend
    participant A as API
    participant DB as Database

    U->>F: Login
    F->>A: POST /auth/login
    A->>DB: Validate user
    DB-->>A: User data
    A-->>F: JWT Token
    F-->>U: Success`
    },
    {
      id: 'database-er',
      name: 'Database Schema',
      definition: `erDiagram
    PET {
        bigint id PK
        string name
        string status
        bigint category_id FK
    }
    
    CATEGORY {
        bigint id PK
        string name
    }
    
    USER {
        bigint id PK
        string username
        string email
    }

    CATEGORY ||--o{ PET : "has many"
    USER ||--o{ PET : "owns"`
    }
  ];

  ngOnInit() {
    this.setDefaultDiagram();
  }

  private setDefaultDiagram() {
    const diagramMap: { [key: string]: string } = {
      'architecture': 'system-overview',
      'flow': 'user-flow', 
      'sequence': 'api-sequence',
      'database': 'database-er'
    };

    this.selectedDiagramId = diagramMap[this.diagramType] || 'system-overview';
    this.onDiagramChange();
  }

  onDiagramChange() {
    const selected = this.quickDiagrams.find(d => d.id === this.selectedDiagramId);
    if (selected) {
      this.currentDiagram = selected.definition;
      this.selectedDiagramTitle = selected.name;
    }
  }
}