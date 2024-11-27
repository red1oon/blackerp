export interface Column {
  name: string;
  type: string;
  mandatory?: boolean;
  length?: number;
  precision?: number;
  scale?: number;
  defaultValue?: string;
}

export interface Table {
  id: string;
  name: string;
  displayName: string;
  description?: string;
  accessLevel: string;
  columns: Column[];
}
