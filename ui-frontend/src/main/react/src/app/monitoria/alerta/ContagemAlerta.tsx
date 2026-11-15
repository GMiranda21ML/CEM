import { useSuspenseQuery } from '@tanstack/react-query';
import { consultaContagem, type Alerta } from '../api';

export function ContagemAlerta({ alerta }: { alerta: Alerta }) {
  const { data: total } = useSuspenseQuery(consultaContagem(alerta.identificador));
  return <>{total} falta(s)</>;
}
