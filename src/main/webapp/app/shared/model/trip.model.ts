import { IStatement } from 'app/shared/model/statement.model';
import { IUser } from 'app/shared/model/user.model';
import { IImportProd } from 'app/shared/model/import-prod.model';
import { IExportProd } from 'app/shared/model/export-prod.model';
import { ITransport } from 'app/shared/model/transport.model';
import { IDriver } from 'app/shared/model/driver.model';
import { IPositioning } from 'app/shared/model/positioning.model';

export interface ITrip {
  id?: number;
  authorizedCapital?: number;
  threshold?: number;
  statements?: IStatement[] | null;
  user?: IUser | null;
  importProd?: IImportProd | null;
  exportProd?: IExportProd | null;
  transport?: ITransport | null;
  driver?: IDriver | null;
  hubPositioning?: IPositioning | null;
}

export const defaultValue: Readonly<ITrip> = {};
