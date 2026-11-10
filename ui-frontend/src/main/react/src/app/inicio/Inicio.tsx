import { useAutenticacao } from '../../d5';

export function Inicio() {
  const { usuario } = useAutenticacao();

  return (
    <div className="container py-4">
      <h1 className="h4">Cem</h1>
      <p className="text-muted">
        Você está autenticado como <strong>{usuario}</strong>.
      </p>
    </div>
  );
}
