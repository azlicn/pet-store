import { Category } from './category.model';

export enum PetStatus {
  AVAILABLE = 'AVAILABLE',
  PENDING = 'PENDING',
  SOLD = 'SOLD'
}

export interface PetPageResponse {
  pets: Pet[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface Pet {
  id?: number;
  name: string;
  description: string;
  category: Category;
  price: number;
  status: PetStatus;
  photoUrls?: string[];
  tags?: string[];
  owner?: {
    id: number;
    email: string;
    firstName: string;
    lastName: string;
  };
  createdAt?: string;
  updatedAt?: string;
  createdBy?: number;
  lastModifiedBy?: number;
}