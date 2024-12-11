export interface Column {
  name: string;
  displayName: string;
  type: string;
  mandatory?: boolean;
  length?: number;
  precision?: number;
  scale?: number;
  defaultValue?: string;
}

export interface CreateTableRequest {
  name: string;
  displayName: string;
  description?: string;
  accessLevel: string;
  columns: Column[];
}

export interface Table {
  id: string;
  name: string;
  displayName: string;
  description?: string;
  accessLevel: string;
  columns: Column[];
}
