export interface Discount {
  id?: number;
  code: string;
  description?: string;
  percentage: number;
  validFrom: string; // ISO date string
  validTo: string;   // ISO date string
  active: boolean;
}