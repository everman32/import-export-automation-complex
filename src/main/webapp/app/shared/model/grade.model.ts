import { IImportProd } from 'app/shared/model/import-prod.model';
import { IExportProd } from 'app/shared/model/export-prod.model';

export interface IGrade {
  id?: number;
  description?: string;
  importProds?: IImportProd[] | null;
  exportProds?: IExportProd[] | null;
}

export const defaultValue: Readonly<IGrade> = {};
